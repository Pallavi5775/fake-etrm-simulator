package com.trading.ctrm.pricing;

import com.trading.ctrm.trade.Trade;
import com.trading.ctrm.trade.TradeRepository;
import com.trading.ctrm.trade.TradeStatus;
import com.trading.ctrm.instrument.Instrument;
import com.trading.ctrm.instrument.InstrumentType;
import com.trading.ctrm.trade.InstrumentRepository;
import com.trading.ctrm.trade.ForwardCurveRepository;
import com.trading.ctrm.yieldcurve.YieldCurveRepository;
import com.trading.ctrm.yieldcurve.YieldCurve;
import com.trading.ctrm.forecast.ForecastPriceRepository;
import com.trading.ctrm.rules.*;
import com.trading.ctrm.rules.MarketContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Batch Valuation Service - Endur-style portfolio revaluation
 * Revalues entire portfolios with parallel processing
 */
@Service
public class BatchValuationService {

    private static final Logger log = LoggerFactory.getLogger(BatchValuationService.class);

    private final TradeRepository tradeRepository;
    private final InstrumentRepository instrumentRepository;
    private final ValuationRunRepository valuationRunRepository;
    private final ValuationResultRepository valuationResultRepository;
    private final PricingEngineFactory pricingEngineFactory;
    private final ExecutorService executorService;

    private final ForwardCurveRepository forwardCurveRepository;
    private final YieldCurveRepository yieldCurveRepository;
    private final ForecastPriceRepository forecastPriceRepository;
    private final MarketContext marketContext;

    public BatchValuationService(
            TradeRepository tradeRepository,
            InstrumentRepository instrumentRepository,
            ValuationRunRepository valuationRunRepository,
            ValuationResultRepository valuationResultRepository,
            PricingEngineFactory pricingEngineFactory,
            ForwardCurveRepository forwardCurveRepository,
            YieldCurveRepository yieldCurveRepository,
            ForecastPriceRepository forecastPriceRepository,
            MarketContext marketContext) {
        this.tradeRepository = tradeRepository;
        this.instrumentRepository = instrumentRepository;
        this.valuationRunRepository = valuationRunRepository;
        this.valuationResultRepository = valuationResultRepository;
        this.pricingEngineFactory = pricingEngineFactory;
        this.forwardCurveRepository = forwardCurveRepository;
        this.yieldCurveRepository = yieldCurveRepository;
        this.forecastPriceRepository = forecastPriceRepository;
        this.marketContext = marketContext;
        // Thread pool sized for parallel processing
        this.executorService = Executors.newFixedThreadPool(10);
    }

    /**
     * Run batch valuation for all active trades
     */
    @Transactional
    public ValuationRun runBatchValuation(LocalDate valuationDate, String portfolioFilter, String startedBy) {
        log.info("Starting batch valuation for date: {}, portfolio: {}", valuationDate, portfolioFilter);

        // Create valuation run
        ValuationRun run = new ValuationRun("Batch Valuation", valuationDate, portfolioFilter, startedBy);
        run = valuationRunRepository.save(run);
        final Long runId = run.getRunId(); // Make final for lambda

        try {
            // Get trades to value
            List<Trade> trades = getTradesToValue(portfolioFilter);
            run.setTotalTrades(trades.size());
            valuationRunRepository.save(run);

            log.info("Found {} trades to value", trades.size());

            // Process trades in parallel
            log.info("Starting parallel valuation of {} trades", trades.size());
            List<CompletableFuture<ValuationResult>> futures = trades.stream()
                .map(trade -> {
                    log.info("[VALUATION-START] Queuing trade {} for parallel valuation", trade.getTradeId());
                    return CompletableFuture.supplyAsync(
                        () -> valuateTrade(trade, valuationDate, runId),
                        executorService
                    );
                })
                .collect(Collectors.toList());

            // Wait for all to complete
            log.info("Waiting for all {} trade valuations to complete", futures.size());
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            log.info("All trade valuations completed, processing results");

            // Count results
            int successful = 0;
            int failed = 0;
            for (CompletableFuture<ValuationResult> future : futures) {
                try {
                    ValuationResult result = future.get();
                    if (result != null) {
                        successful++;
                        log.info("[VALUATION-SUCCESS] Trade {} valuation completed successfully", result.getTradeId());
                    } else {
                        failed++; 
                        log.error("[VALUATION-FAILED] Trade valuation returned null result");
                    }
                } catch (Exception e) {
                    failed++;
                    log.error("[VALUATION-FAILED] Exception during trade valuation", e);
                }
            }

            log.info("Batch valuation results: {} successful, {} failed out of {} total trades", 
                    successful, failed, trades.size());

            run.setSuccessfulCount(successful);
            run.setFailedCount(failed);
            run.setStatus("COMPLETED");
            run.setCompletedAt(LocalDateTime.now());

            log.info("Batch valuation completed: {} successful, {} failed", successful, failed);

        } catch (Exception e) {
            log.error("Batch valuation failed", e);
            run.setStatus("FAILED");
            run.setCompletedAt(LocalDateTime.now());
        }

        return valuationRunRepository.save(run);
    }

    /**
     * Value a single trade
     */
    private ValuationResult valuateTrade(Trade trade, LocalDate valuationDate, Long runId) {
        long startTime = System.currentTimeMillis();
        String tradeId = trade.getTradeId();
        
        try {
            log.info("[VALUATION-START] Starting valuation for trade: {} (runId: {})", tradeId, runId);

            // Load instrument from trade
            Instrument instrument = trade.getInstrument();
            log.info("[VALUATION-DETAIL] Trade {} - Instrument: {} (type: {})", 
                     tradeId, instrument.getInstrumentCode(), instrument.getInstrumentType());

            // Get pricing engine
            PricingEngine engine = pricingEngineFactory.getEngine(instrument);
            log.info("[VALUATION-DETAIL] Trade {} - Selected pricing engine: {}", 
                     tradeId, engine.getClass().getSimpleName());

            // Build valuation context with correct market data for engine type
            ValuationContext context;
            InstrumentType type = instrument.getInstrumentType();
            log.info("[VALUATION-DETAIL] Trade {} - Building context for instrument type: {}", tradeId, type);
            
            switch (type) {
                case POWER_FORWARD -> {
                    log.info("[VALUATION-DETAIL] Trade {} - Processing POWER_FORWARD instrument", tradeId);
                    
                    // For power forwards, use MarketContext for consistent data access
                    BigDecimal yieldRate = marketContext.getYieldCurve(trade.getInstrument(), valuationDate);
                    
                    if (yieldRate == null) {
                        log.error("[VALUATION-ERROR] Trade {} - No yield curve found for instrument: {} on {}", 
                                 tradeId, trade.getInstrument().getInstrumentCode(), valuationDate);
                        return null;
                    }
                    
                    log.info("[VALUATION-DETAIL] Trade {} - Yield rate found: {}", tradeId, yieldRate);
                    
                    PricingContext pricingCtx = PricingContext.of(
                        "POWER_FORWARD",
                        "ACT_365",
                        "CONTINUOUS",
                        "CASH",
                        yieldRate,
                        null, // volatility
                        null, // discountRate
                        null, // yearsToExpiry
                        null, // cashFlows
                        null  // cashFlowTimes
                    );
                    context = ValuationContext.builder()
                        .trade(TradeContext.fromTrade(trade))
                        .market(null) // Market data now handled directly in PricingContext
                        .pricing(pricingCtx)
                        .risk(RiskContext.fromTrade(trade))
                        .accounting(AccountingContext.fromTrade(trade))
                        .credit(CreditContext.fromTrade(trade))
                        .audit(AuditContext.fromTrade(trade))
                        .valuationDate(valuationDate)
                        .build();
                }
                case OPTION -> {
                    log.info("[VALUATION-DETAIL] Trade {} - Processing OPTION instrument", tradeId);
                    
                    // For options, determine the appropriate date for forward curve lookup
                    LocalDate forwardDate = valuationDate; // default to valuation date
                    if (trade.getInstrument() instanceof com.trading.ctrm.instrument.CommodityOptionInstrument) {
                        com.trading.ctrm.instrument.CommodityOptionInstrument option = 
                            (com.trading.ctrm.instrument.CommodityOptionInstrument) trade.getInstrument();
                        if (option.getExpiryDate() != null) {
                            forwardDate = option.getExpiryDate();
                            log.info("[VALUATION-DETAIL] Trade {} - Using option expiry date: {}", tradeId, forwardDate);
                        }
                    }
                    
                    // For options, use MarketContext for consistent data access
                    BigDecimal forwardPrice = marketContext.getForwardCurve(trade.getInstrument(), forwardDate);
                    BigDecimal volatility = marketContext.getVolatility(trade.getInstrument(), valuationDate);
                    BigDecimal discountRate = marketContext.getYieldCurve(trade.getInstrument(), valuationDate);
                    
                    if (forwardPrice == null) {
                        log.error("[VALUATION-ERROR] Trade {} - No forward curve found for instrument: {} on {}", 
                                 tradeId, trade.getInstrument().getInstrumentCode(), forwardDate);
                        return null;
                    }
                    if (volatility == null) {
                        log.error("[VALUATION-ERROR] Trade {} - No volatility found for instrument: {} on {}", 
                                 tradeId, trade.getInstrument().getInstrumentCode(), valuationDate);
                        return null;
                    }
                    if (discountRate == null) {
                        log.error("[VALUATION-ERROR] Trade {} - No yield curve found for instrument: {} on {}", 
                                 tradeId, trade.getInstrument().getInstrumentCode(), valuationDate);
                        return null;
                    }
                    
                    log.info("[VALUATION-DETAIL] Trade {} - Market data found - Forward: {}, Vol: {}, Discount: {}", 
                             tradeId, forwardPrice, volatility, discountRate);
                    
                    // Set pricing date on market context for the pricing engine
                    marketContext.setPricingDate(valuationDate);
                    
                    PricingContext pricingCtx = PricingContext.of(
                        "Black76",
                        "ACT_365",
                        "CONTINUOUS",
                        "CASH",
                        forwardPrice,
                        volatility,
                        discountRate,
                        null, // yearsToExpiry
                        null, // cashFlows
                        null  // cashFlowTimes
                    );
                    context = ValuationContext.builder()
                        .trade(TradeContext.fromTrade(trade))
                        .market(marketContext) // Pass the MarketContext for pricing engines
                        .pricing(pricingCtx)
                        .risk(RiskContext.fromTrade(trade))
                        .accounting(AccountingContext.fromTrade(trade))
                        .credit(CreditContext.fromTrade(trade))
                        .audit(AuditContext.fromTrade(trade))
                        .valuationDate(valuationDate)
                        .build();
                }

                
                case RENEWABLE_PPA -> {
                    log.info("[VALUATION-DETAIL] Trade {} - Processing RENEWABLE_PPA instrument", tradeId);
                    
                    // For renewable PPA, use forecast prices from MarketContext
                    BigDecimal forecastPrice = marketContext.getForecastPrice(trade.getInstrument(), valuationDate);
                    if (forecastPrice == null) {
                        log.error("[VALUATION-ERROR] Trade {} - No forecast price found for instrument: {} on {}", 
                                 tradeId, trade.getInstrument().getInstrumentCode(), valuationDate);
                        return null;
                    }
                    
                    log.info("[VALUATION-DETAIL] Trade {} - Forecast price found: {}", tradeId, forecastPrice);
                    PricingContext pricingCtx = PricingContext.of(
                        "RENEWABLE_FORECAST",
                        "ACT_365",
                        "CONTINUOUS",
                        "CASH",
                        forecastPrice,
                        null, // volatility
                        null, // discountRate
                        null, // yearsToExpiry
                        null, // cashFlows
                        null  // cashFlowTimes
                    );
                    context = ValuationContext.builder()
                        .trade(TradeContext.fromTrade(trade))
                        .market(null) // Market data now handled directly in PricingContext
                        .pricing(pricingCtx)
                        .risk(RiskContext.fromTrade(trade))
                        .accounting(AccountingContext.fromTrade(trade))
                        .credit(CreditContext.fromTrade(trade))
                        .audit(AuditContext.fromTrade(trade))
                        .valuationDate(valuationDate)
                        .build();
                }

                
                case GAS_FORWARD, COMMODITY_SWAP, FREIGHT -> {
                    log.info("[VALUATION-DETAIL] Trade {} - Processing {} instrument", tradeId, type);
                    
                    // For gas forwards, commodity swaps, and freight, use yield curves directly from repository
                    String instrumentCode = String.valueOf(trade.getInstrument().getId());
                    log.info("[VALUATION-DETAIL] Trade {} - Looking for yield curve with instrumentCode: {}", tradeId, instrumentCode);
                    
                    // info: Break down the stream operation for easier debugging
                    List<YieldCurve> allCurves = yieldCurveRepository.findAll();
                    log.info("[VALUATION-DETAIL] Trade {} - Found {} total yield curves in repository", tradeId, allCurves.size());
                    
                    List<YieldCurve> matchingCurves = allCurves.stream()
                        .filter(yc -> {
                            boolean nameMatches = yc.getCurveName().equals(instrumentCode);
                            boolean dateValid = yc.getDate().isBefore(valuationDate) || yc.getDate().equals(valuationDate);
                            log.info("[VALUATION-DETAIL] Trade {} - Checking curve: name='{}' (matches:{}) date='{}' (valid:{})", 
                                     tradeId, yc.getCurveName(), nameMatches, yc.getDate(), dateValid);
                            return nameMatches && dateValid;
                        })
                        .collect(Collectors.toList());
                    
                    log.info("[VALUATION-DETAIL] Trade {} - Found {} curves matching name '{}' and date <= '{}'", 
                             tradeId, matchingCurves.size(), instrumentCode, valuationDate);
                    
                    Optional<YieldCurve> latestCurve = matchingCurves.stream()
                        .max((a, b) -> a.getDate().compareTo(b.getDate()));
                    
                    if (latestCurve.isPresent()) {
                        YieldCurve curve = latestCurve.get();
                        log.info("[VALUATION-DETAIL] Trade {} - Latest curve found: name='{}', date='{}', yield={}", 
                                 tradeId, curve.getCurveName(), curve.getDate(), curve.getYield());
                        BigDecimal discountRate = curve.getYield() != null ? BigDecimal.valueOf(curve.getYield()) : null;
                        log.info("[VALUATION-DETAIL] Trade {} - Converted discount rate: {}", tradeId, discountRate);
                    } else {
                        log.info("[VALUATION-DETAIL] Trade {} - No matching yield curve found", tradeId);
                    }
                    
                    BigDecimal discountRate = yieldCurveRepository.findAll().stream()
                        .filter(yc -> yc.getCurveName().equals(instrumentCode) && 
                               (yc.getDate().isBefore(valuationDate) || yc.getDate().equals(valuationDate)))
                        .max((a, b) -> a.getDate().compareTo(b.getDate()))
                        .map(yc -> yc.getYield() != null ? BigDecimal.valueOf(yc.getYield()) : null)
                        .orElse(null);
                    if (discountRate == null) {
                        log.error("[VALUATION-ERROR] Trade {} - No yield curve found for instrument: {} on {}", 
                                 tradeId, trade.getInstrument().getId(), valuationDate);
                        return null;
                    }
                    
                    log.info("[VALUATION-DETAIL] Trade {} - Discount rate found: {}", tradeId, discountRate);
                    PricingContext pricingCtx = PricingContext.of(
                        "DCF",
                        "ACT_365",
                        "CONTINUOUS",
                        "CASH",
                        null, // forwardPrice
                        null, // volatility
                        discountRate,
                        null, // yearsToExpiry
                        null, // cashFlows
                        null  // cashFlowTimes
                    );
                    context = ValuationContext.builder()
                        .trade(TradeContext.fromTrade(trade))
                        .market(null) // Market data now handled directly in PricingContext
                        .pricing(pricingCtx)
                        .risk(RiskContext.fromTrade(trade))
                        .accounting(AccountingContext.fromTrade(trade))
                        .credit(CreditContext.fromTrade(trade))
                        .audit(AuditContext.fromTrade(trade))
                        .valuationDate(valuationDate)
                        .build();
                }
                default -> {
                    log.info("[VALUATION-DETAIL] Trade {} - Processing {} instrument with default context", tradeId, type);
                    
                    context = ValuationContext.builder()
                        .trade(TradeContext.fromTrade(trade))
                        .market(null) // Market data now handled directly in PricingContext
                        .pricing(PricingContext.fromTrade(trade))
                        .risk(RiskContext.fromTrade(trade))
                        .accounting(AccountingContext.fromTrade(trade))
                        .credit(CreditContext.fromTrade(trade))
                        .audit(AuditContext.fromTrade(trade))
                        .valuationDate(valuationDate)
                        .build();
                }
            }

            log.info("[VALUATION-DETAIL] Trade {} - Context built, executing pricing engine", tradeId);
            
            log.info("[VALUATION-DETAIL] Trade {} - Executing pricing engine: {}", tradeId, engine.getClass().getSimpleName());
            ValuationResult result = engine.price(trade, instrument, context);
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("[VALUATION-SUCCESS] Trade {} - Valuation completed successfully in {}ms - MTM: {}", 
                    tradeId, duration, result.getMtmTotal());

            // Set run metadata
            result.setValuationRunId(runId);
            result.setPricingDate(valuationDate);
            result.setTradeId(Long.valueOf(trade.getId())); // Set the trade ID
            result.setInstrumentId(Long.valueOf(instrument.getId())); // Set the instrument ID

            // Save result
            ValuationResult savedResult = valuationResultRepository.save(result);
            log.info("[VALUATION-DETAIL] Trade {} - Result saved with ID: {}", tradeId, savedResult.getResultId());
            
            return savedResult;

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("[VALUATION-FAILED] Trade {} - Valuation failed after {}ms - Error: {}", 
                     tradeId, duration, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Get trades to value based on portfolio filter
     */
    private List<Trade> getTradesToValue(String portfolioFilter) {
        List<TradeStatus> statuses = com.trading.ctrm.trade.TradeStatusUtil.getValuableStatuses();
        if (portfolioFilter == null || portfolioFilter.isBlank() || portfolioFilter.equals("ALL")) {
            return tradeRepository.findByStatusIn(statuses);
        } else {
            return tradeRepository.findByPortfolioAndStatusIn(portfolioFilter, statuses);
        }
    }

    /**
     * Get latest valuation run for a date
     */
    public ValuationRun getLatestRun(LocalDate valuationDate) {
        return valuationRunRepository
            .findTopByValuationDateAndStatusOrderByStartedAtDesc(valuationDate, "COMPLETED")
            .orElse(null);
    }

    /**
     * Get all runs
     */
    public List<ValuationRun> getRecentRuns() {
        return valuationRunRepository.findTop10ByOrderByStartedAtDesc();
    }

    /**
     * Get valuation results for a specific date
     */
    public List<ValuationResult> getValuationResultsForDate(LocalDate valuationDate) {
        return valuationResultRepository.findByPricingDate(valuationDate);
    }

    /**
     * Get all valuation results
     */
    public List<ValuationResult> getAllValuationResults() {
        return valuationResultRepository.findAll();
    }

    /**
     * Get valuation results for a specific trade
     */
    public List<ValuationResult> getValuationResultsForTrade(Long tradeId) {
        return valuationResultRepository.findByTradeId(tradeId);
    }

    /**
     * Get all trades
     */
    public List<com.trading.ctrm.trade.Trade> getAllTrades() {
        return tradeRepository.findAll();
    }
}
