package com.trading.ctrm.trade;

import org.springframework.stereotype.Service;

import com.trading.ctrm.deals.DealTemplate;
import com.trading.ctrm.deals.DealTemplateRepository;
import com.trading.ctrm.instrument.Instrument;
import com.trading.ctrm.lifestyle.TradeLifecycleEngine;
import com.trading.ctrm.rules.ApprovalExecutionService;
import com.trading.ctrm.rules.ApprovalRouting;
import com.trading.ctrm.rules.ApprovalRule;
import com.trading.ctrm.rules.ApprovalRuleEngine;
import com.trading.ctrm.rules.TradeContext;
import com.trading.ctrm.rules.ValuationContext;
import com.trading.ctrm.rules.MarketContext;
import com.trading.ctrm.rules.PricingContext;
import com.trading.ctrm.rules.RiskContext;
import com.trading.ctrm.rules.AccountingContext;
import com.trading.ctrm.rules.CreditContext;
import com.trading.ctrm.rules.AuditContext;
import com.trading.ctrm.trade.EnumType.BuySell;
import com.trading.ctrm.trade.dto.TradeEventRequest;
import com.trading.ctrm.trade.dto.ValuationConfigRequest;
import com.trading.ctrm.trade.dto.MultiLegTradeRequest;
import com.trading.ctrm.trade.dto.TradeLegRequest;
import com.trading.ctrm.trade.dto.TradeEventDto;

import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

import org.springframework.lang.NonNull;

@Service
public class TradeService {

    private final TradeRepository tradeRepository;
    private final InstrumentRepository instrumentRepository;
    private final PortfolioPositionService portfolioPositionService;
    private final TradeLifecycleEngine tradeLifecycleEngine;
    private final DealTemplateRepository templateRepo;
    private final ApprovalRuleEngine approvalRuleEngine;
    private final ApprovalExecutionService approvalExecutionService;
    private final TradeLegRepository tradeLegRepository;
    private final ForwardCurveRepository forwardCurveRepository;
    private final TradeEventRepository tradeEventRepository;
    private final com.trading.ctrm.rules.MarketContext marketContext;

    public TradeService(
            TradeRepository tradeRepository,
            InstrumentRepository instrumentRepository,
            PortfolioPositionService portfolioPositionService,
            TradeLifecycleEngine tradeLifecycleEngine,
            DealTemplateRepository templateRepo,
            ApprovalRuleEngine approvalRuleEngine,
            ApprovalExecutionService approvalExecutionService,
            TradeLegRepository tradeLegRepository,
            ForwardCurveRepository forwardCurveRepository,
            TradeEventRepository tradeEventRepository,
            com.trading.ctrm.rules.MarketContext marketContext
    ) {
        this.tradeRepository = tradeRepository;
        this.instrumentRepository = instrumentRepository;
        this.portfolioPositionService = portfolioPositionService;
        this.tradeLifecycleEngine = tradeLifecycleEngine;
        this.templateRepo = templateRepo;
        this.approvalRuleEngine = approvalRuleEngine;
        this.approvalExecutionService = approvalExecutionService;
        this.tradeLegRepository = tradeLegRepository;
        this.forwardCurveRepository = forwardCurveRepository;
        this.tradeEventRepository = tradeEventRepository;
        this.marketContext = marketContext;
    }

    public Trade bookTrade(TradeEventRequest req) {
        if (req.getInstrumentSymbol() == null || req.getInstrumentSymbol().isBlank()) {
            throw new IllegalArgumentException("Instrument symbol is required");
        }

        Instrument instrument = instrumentRepository
                .findByInstrumentCode(req.getInstrumentSymbol());
                

        Trade trade = new Trade();
        trade.setTradeId(req.getTradeId());
        trade.setInstrument(instrument);
        trade.setPortfolio(req.getPortfolio());
        trade.setCounterparty(req.getCounterparty());
        trade.setQuantity(req.getQuantity());
        trade.setPrice(req.getPrice());
        trade.setBuySell(req.getBuySell());

        // ✅ SET INITIAL STATUS
        trade.setStatus(TradeStatus.CREATED);

        Trade saved = tradeRepository.save(trade);

        // Now apply lifecycle event
        tradeLifecycleEngine.applyEvent(
                saved,
                TradeEventType.CREATED,
                saved.getCreatedBy(),
                "UI"
        );

        portfolioPositionService.updatePosition(saved);
        return saved;
    }
    


@Transactional
public Trade bookFromTemplate(
        Long templateId,
        BigDecimal quantityOverride,
        BuySell buySell,
        String counterparty,
        String portfolio,
        String createdByUser,
        LocalDate tradeDate,
        ValuationConfigRequest valuationConfig
) {

    DealTemplate template = templateRepo.findById(templateId)
        .orElseThrow(() -> new IllegalArgumentException("DealTemplate not found"));

    Trade trade = new Trade();

    // ===== TEMPLATE FIELDS =====
    trade.setInstrument(template.getInstrument());
    trade.setPrice(template.getDefaultPrice());
    trade.setTemplateId(template.getId());

    // ===== FO INPUT =====
    trade.setBuySell(buySell);
    trade.setCounterparty(counterparty);
    trade.setPortfolio(portfolio);
    trade.setQuantity(
        quantityOverride != null
            ? quantityOverride
            : template.getDefaultQuantity()
    );

    // ===== SYSTEM FIELDS =====
    String tradeIdPrefix = "TRD-" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
    long tradeCount = tradeRepository.count() + 1;
    trade.setTradeId(tradeIdPrefix + "-" + String.format("%04d", tradeCount));
    trade.setCreatedAt(LocalDateTime.now());
    trade.setStatus(TradeStatus.CREATED);
    trade.setCreatedBy(createdByUser != null ? createdByUser : "UNKNOWN_USER");
    trade.setTradeDate(tradeDate != null ? tradeDate : LocalDate.now());

    // ===== SAVE TRADE TO GENERATE ID (needed for valuation context) =====
    trade = tradeRepository.save(trade);

    // ===== AUTO-APPROVAL CHECK =====
    if (template.isAutoApprovalAllowed()) {
        trade.setStatus(TradeStatus.APPROVED);
        trade.setPendingApprovalRole(null);
        return tradeRepository.save(trade);
    }

    // ===== APPROVAL EVALUATION =====
    ValuationContext valuationCtx = buildValuationContext(trade, valuationConfig);

    TradeContext ctx = valuationCtx.trade();

    System.out.println("🔍 Evaluating approval rules for trade: " + trade.getTradeId());
    System.out.println("   Quantity: " + ctx.quantity());
    System.out.println("   Counterparty: " + ctx.counterparty());
    System.out.println("   Portfolio: " + ctx.portfolio());
    System.out.println("   InstrumentType: " + ctx.instrumentType());

    Optional<ApprovalRule> matchedRule =
        approvalRuleEngine.evaluate(ctx, "TRADE_BOOK");

    System.out.println("   Rule matched: " + matchedRule.isPresent());

    if (matchedRule.isPresent()) {

        ApprovalRule rule = matchedRule.get();

        approvalExecutionService.createPendingApproval(
            ctx,
            rule,
            "TRADE_BOOK"
        );

        trade.setStatus(TradeStatus.PENDING_APPROVAL);
        trade.setMatchedRuleId(rule.getRuleId());
        trade.setCurrentApprovalLevel(1);
        trade.setPendingApprovalRole(
            rule.getRouting().stream()
                .filter(r -> r.getApprovalLevel() == 1)
                .findFirst()
                .map(ApprovalRouting::getApprovalRole)
                .orElse(null)
        );

    } else {
        // No matching rule found - trade requires manual approval
        trade.setStatus(TradeStatus.PENDING_APPROVAL);
        trade.setPendingApprovalRole("SENIOR_TRADER"); // Default approver when no rule matches
    }

    return tradeRepository.save(trade);
}



    public Trade applyEvent(String tradeId, TradeEventType eventType) {

        Trade trade = tradeRepository
                .findByTradeId(tradeId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Trade not found: " + tradeId)
                );

        Trade updated = tradeLifecycleEngine.applyEvent(
                trade,
                eventType,
                trade.getCreatedBy(),
                "UI"
        );

        if (eventType == TradeEventType.AMENDED ||
            eventType == TradeEventType.PRICED) {

            portfolioPositionService.updatePosition(updated);
        }

        return updated;
    }

    public Trade findByTradeId(String tradeId) {
        // Try parsing as Long first (numeric database ID)
        try {
            Long id = Long.parseLong(tradeId);
            return tradeRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Trade not found with id: " + tradeId));
        } catch (NumberFormatException e) {
            // Not a number, lookup by business trade ID
            return tradeRepository.findByTradeId(tradeId)
                    .orElseThrow(() -> new IllegalArgumentException("Trade not found: " + tradeId));
        }
    }

    /**
     * Builds ValuationContext from trade and optional UI configuration
     */
    private ValuationContext buildValuationContext(Trade trade, ValuationConfigRequest config) {
        if (config == null) {
            // Use defaults - create market context with trade date
            com.trading.ctrm.rules.MarketContext marketCtx = new com.trading.ctrm.rules.MarketContext(
                marketContext.marketDataSet(),
                trade.getTradeDate() != null ? trade.getTradeDate() : java.time.LocalDate.now(),
                marketContext.curveSet(),
                marketContext.fxScenario(),
                trade.getInstrument().getInstrumentType().name() + "_VOL",
                marketContext.forecastPrices(),
                marketContext.getForwardCurveRepository(),
                marketContext.getYieldCurveRepository(),
                marketContext.getVolatilityRepository(),
                marketContext.getForecastPriceRepository(),
                marketContext.getMarketPriceRepository(),
                marketContext.getWeatherDataRepository(),
                marketContext.getGenerationForecastRepository(),
                marketContext.getVolatilitySurfaceRepository(),
                marketContext.getPriceCurveRepository(),
                marketContext.getMarketCurveRepository()
            );

            return ValuationContext.builder()
                .trade(TradeContext.fromTrade(trade))
                .market(marketCtx)
                .pricing(PricingContext.fromTrade(trade))
                .risk(RiskContext.fromTrade(trade))
                .accounting(AccountingContext.fromTrade(trade))
                .credit(CreditContext.fromTrade(trade))
                .audit(AuditContext.fromTrade(trade))
                .build();
        }

        // Use custom values from UI
        com.trading.ctrm.rules.MarketContext marketCtx = new com.trading.ctrm.rules.MarketContext(
            config.getMarketDataSet() != null ? config.getMarketDataSet() : marketContext.marketDataSet(),
            config.getPricingDate() != null ? config.getPricingDate() : (trade.getTradeDate() != null ? trade.getTradeDate() : java.time.LocalDate.now()),
            config.getCurveSet() != null ? config.getCurveSet() : marketContext.curveSet(),
            config.getFxScenario() != null ? config.getFxScenario() : marketContext.fxScenario(),
            config.getVolatilitySurface() != null ? config.getVolatilitySurface() : (trade.getInstrument().getInstrumentType().name() + "_VOL"),
            marketContext.forecastPrices(),
            marketContext.getForwardCurveRepository(),
            marketContext.getYieldCurveRepository(),
            marketContext.getVolatilityRepository(),
            marketContext.getForecastPriceRepository(),
            marketContext.getMarketPriceRepository(),
            marketContext.getWeatherDataRepository(),
            marketContext.getGenerationForecastRepository(),
            marketContext.getVolatilitySurfaceRepository(),
            marketContext.getPriceCurveRepository(),
            marketContext.getMarketCurveRepository()
        );

        return ValuationContext.builder()
            .trade(TradeContext.fromTrade(trade))
            .market(marketCtx)
            .pricing(PricingContext.fromTrade(
                trade,
                config.getPricingModel(),
                config.getDayCount(),
                config.getCompounding(),
                config.getSettlementType()
            ))
            .risk(RiskContext.fromTrade(
                trade,
                config.getEvaluationPurpose(),
                config.getGreeksEnabled(),
                config.getShockScenario(),
                config.getAggregationLevel()
            ))
            .accounting(AccountingContext.fromTrade(
                trade,
                config.getAccountingBook(),
                config.getPnlType(),
                config.getIncludeAccruals()
            ))
            .credit(CreditContext.fromTrade(
                trade,
                config.getCreditCurve(),
                config.getNettingSet(),
                config.getCollateralAgreement()
            ))
            .audit(AuditContext.fromTrade(
                trade,
                config.getUser(),
                config.getLegalEntity(),
                config.getSourceSystem()
            ))
            .build();
    }

    /**
     * Book a multi-leg trade (spread, butterfly, etc.)
     */
    @Transactional
    public Trade bookMultiLegTrade(MultiLegTradeRequest request) {
        if (request.getLegs() == null || request.getLegs().size() < 2) {
            throw new IllegalArgumentException("Multi-leg trade must have at least 2 legs");
        }

        // Create parent trade
        Trade trade = new Trade();
        
        // Generate trade ID
        String tradeIdPrefix = "TRD-" + LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        long tradeCount = tradeRepository.count() + 1;
        trade.setTradeId(tradeIdPrefix + "-" + String.format("%04d", tradeCount));
        
        // Set multi-leg fields
        trade.setIsMultiLeg(true);
        trade.setStrategyType(request.getStrategyType() != null ? 
            request.getStrategyType() : StrategyType.CUSTOM);
        
        // Set first leg's instrument as primary (for display)
        TradeLegRequest firstLeg = request.getLegs().get(0);
        Instrument firstInstrument = resolveInstrument(firstLeg);
        trade.setInstrument(firstInstrument);
        
        // Calculate net quantity and average price for parent
        BigDecimal netQuantity = BigDecimal.ZERO;
        BigDecimal weightedPrice = BigDecimal.ZERO;
        for (TradeLegRequest legReq : request.getLegs()) {
            BigDecimal legQty = legReq.getQuantity();
            if (legReq.getBuySell() == BuySell.SELL) {
                legQty = legQty.negate();
            }
            netQuantity = netQuantity.add(legQty);
            weightedPrice = weightedPrice.add(legReq.getPrice().multiply(legReq.getQuantity()));
        }
        
        BigDecimal totalQuantity = request.getLegs().stream()
            .map(TradeLegRequest::getQuantity)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        trade.setQuantity(totalQuantity);
        trade.setPrice(weightedPrice.divide(totalQuantity, 4, java.math.RoundingMode.HALF_UP));
        trade.setBuySell(netQuantity.compareTo(BigDecimal.ZERO) >= 0 ? BuySell.BUY : BuySell.SELL);
        
        // Set common fields
        trade.setPortfolio(request.getPortfolio());
        trade.setCounterparty(request.getCounterparty());
        trade.setCreatedBy(request.getCreatedByUser());
        trade.setTradeDate(request.getTradeDate() != null ? request.getTradeDate() : LocalDate.now());
        
        // Set temporary status (will be updated by approval workflow)
        trade.setStatus(TradeStatus.PENDING_APPROVAL);
        
        // Save parent trade to generate ID
        trade = tradeRepository.save(trade);
        
        // Create and save legs
        List<TradeLeg> legs = new ArrayList<>();
        int legNumber = 1;
        for (TradeLegRequest legReq : request.getLegs()) {
            TradeLeg leg = new TradeLeg();
            leg.setTradeId(trade.getTradeId());
            leg.setLegNumber(legNumber++);
            leg.setInstrument(resolveInstrument(legReq));
            leg.setBuySell(legReq.getBuySell());
            leg.setQuantity(legReq.getQuantity());
            leg.setPrice(legReq.getPrice());
            leg.setRatio(legReq.getRatio() != null ? legReq.getRatio() : BigDecimal.ONE);
            leg.setDeliveryDate(legReq.getDeliveryDate());
            
            legs.add(tradeLegRepository.save(leg));
        }
        
        trade.setLegs(legs);
        
        // Route through approval workflow
        TradeContext ctx = TradeContext.fromTrade(trade);
        
        Optional<ApprovalRule> matchedRule = approvalRuleEngine.evaluate(ctx, "TRADE_BOOK");
        
        if (matchedRule.isPresent()) {
            ApprovalRule rule = matchedRule.get();
            
            approvalExecutionService.createPendingApproval(ctx, rule, "TRADE_BOOK");
            
            trade.setStatus(TradeStatus.PENDING_APPROVAL);
            trade.setMatchedRuleId(rule.getRuleId());
            trade.setCurrentApprovalLevel(1);
            trade.setPendingApprovalRole(
                rule.getRouting().stream()
                    .filter(r -> r.getApprovalLevel() == 1)
                    .findFirst()
                    .map(ApprovalRouting::getApprovalRole)
                    .orElse(null)
            );
        } else {
            // No matching rule - requires manual approval
            trade.setStatus(TradeStatus.PENDING_APPROVAL);
            trade.setPendingApprovalRole("SENIOR_TRADER");
        }
        
        trade = tradeRepository.save(trade);
        
        // Update positions for each leg
        for (TradeLeg leg : legs) {
            portfolioPositionService.updatePositionForLeg(trade, leg);
        }
        
        return trade;
    }
    
    /**
     * Resolve instrument from leg request
     */
    private Instrument resolveInstrument(TradeLegRequest legReq) {
        if (legReq.getInstrumentId() != null) {
            return instrumentRepository.findById(legReq.getInstrumentId())
                .orElseThrow(() -> new IllegalArgumentException(
                    "Instrument not found: " + legReq.getInstrumentId()));
        } else if (legReq.getInstrumentCode() != null) {
            return instrumentRepository.findByInstrumentCode(legReq.getInstrumentCode());
        } else {
            throw new IllegalArgumentException("Either instrumentId or instrumentCode must be provided");
        }
    }
    
    /**
     * Get legs for a multi-leg trade
     */
    public List<TradeLeg> getTradeLegs(String tradeId) {
        return tradeLegRepository.findByTradeIdOrderByLegNumber(tradeId);
    }
    
    /**
     * Find latest forward curve for an instrument and delivery date
     */
    public ForwardCurve findLatestForwardCurve(Instrument instrument, LocalDate deliveryDate) {
        return forwardCurveRepository
            .findLatestByInstrumentAndDeliveryDate(instrument, deliveryDate)
            .orElseThrow(() -> new RuntimeException(
                "Forward curve not found for " + instrument.getInstrumentCode() + 
                " on " + deliveryDate));
    }

    /**
     * Save or update a trade
     */
    @Transactional
    public Trade saveTrade(Trade trade) {
        return tradeRepository.save(trade);
    }

    public List<TradeEventDto> getTradeEvents(String tradeId) {
        Trade trade = tradeRepository.findByTradeId(tradeId)
            .orElseThrow(() -> new IllegalArgumentException("Trade not found: " + tradeId));
        List<TradeEvent> events = new ArrayList<>();
        if (trade.getId() != null) {
            events = tradeEventRepository.findByTradeIdOrderByCreatedAt(trade.getId());
        }
        List<TradeEventDto> dtos = new ArrayList<>();
        for (TradeEvent event : events) {
            dtos.add(new TradeEventDto(
                event.getId(),
                event.getEventType(),
                event.getTriggeredBy(),
                event.getSource(),
                event.getCreatedAt()
            ));
        }
        return dtos;
    }

    
}
