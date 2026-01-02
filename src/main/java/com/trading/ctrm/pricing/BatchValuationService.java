package com.trading.ctrm.pricing;

import com.trading.ctrm.trade.Trade;
import com.trading.ctrm.trade.TradeRepository;
import com.trading.ctrm.instrument.Instrument;
import com.trading.ctrm.trade.InstrumentRepository;
import com.trading.ctrm.rules.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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

    public BatchValuationService(
            TradeRepository tradeRepository,
            InstrumentRepository instrumentRepository,
            ValuationRunRepository valuationRunRepository,
            ValuationResultRepository valuationResultRepository,
            PricingEngineFactory pricingEngineFactory) {
        this.tradeRepository = tradeRepository;
        this.instrumentRepository = instrumentRepository;
        this.valuationRunRepository = valuationRunRepository;
        this.valuationResultRepository = valuationResultRepository;
        this.pricingEngineFactory = pricingEngineFactory;
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
            List<CompletableFuture<ValuationResult>> futures = trades.stream()
                .map(trade -> CompletableFuture.supplyAsync(
                    () -> valuateTrade(trade, valuationDate, runId),
                    executorService
                ))
                .collect(Collectors.toList());

            // Wait for all to complete
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            // Count results
            int successful = 0;
            int failed = 0;
            for (CompletableFuture<ValuationResult> future : futures) {
                try {
                    ValuationResult result = future.get();
                    if (result != null) {
                        successful++;
                    } else {
                        failed++;
                    }
                } catch (Exception e) {
                    failed++;
                }
            }

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
        try {
            log.debug("Valuing trade: {}", trade.getTradeId());

            // Load instrument from trade
            Instrument instrument = trade.getInstrument();

            // Build valuation context (using defaults)
            ValuationContext context = ValuationContext.builder()
                .trade(TradeContext.fromTrade(trade))
                .market(MarketContext.fromTrade(trade))
                .pricing(PricingContext.fromTrade(trade))
                .risk(RiskContext.fromTrade(trade))
                .accounting(AccountingContext.fromTrade(trade))
                .credit(CreditContext.fromTrade(trade))
                .audit(AuditContext.fromTrade(trade))
                .build();

            // Get pricing engine and calculate
            PricingEngine engine = pricingEngineFactory.getEngine(instrument.getInstrumentType());
            ValuationResult result = engine.price(trade, instrument, context);

            // Set run metadata
            result.setValuationRunId(runId);
            result.setPricingDate(valuationDate);

            // Save result
            return valuationResultRepository.save(result);

        } catch (Exception e) {
            log.error("Failed to value trade: {}", trade.getTradeId(), e);
            return null;
        }
    }

    /**
     * Get trades to value based on portfolio filter
     */
    private List<Trade> getTradesToValue(String portfolioFilter) {
        if (portfolioFilter == null || portfolioFilter.isBlank() || portfolioFilter.equals("ALL")) {
            return tradeRepository.findByStatus(com.trading.ctrm.trade.TradeStatus.BOOKED);
        } else {
            return tradeRepository.findByPortfolioAndStatus(portfolioFilter, com.trading.ctrm.trade.TradeStatus.BOOKED);
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
}
