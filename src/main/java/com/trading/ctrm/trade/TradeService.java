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

import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

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

    public TradeService(
            TradeRepository tradeRepository,
            InstrumentRepository instrumentRepository,
            PortfolioPositionService portfolioPositionService,
            TradeLifecycleEngine tradeLifecycleEngine,
            DealTemplateRepository templateRepo,
            ApprovalRuleEngine approvalRuleEngine,
            ApprovalExecutionService approvalExecutionService
    ) {
        this.tradeRepository = tradeRepository;
        this.instrumentRepository = instrumentRepository;
        this.portfolioPositionService = portfolioPositionService;
        this.tradeLifecycleEngine = tradeLifecycleEngine;
        this.templateRepo = templateRepo;
        this.approvalRuleEngine = approvalRuleEngine;
        this.approvalExecutionService = approvalExecutionService;
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
                getCurrentUser(),
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
    trade.setTradeId(UUID.randomUUID().toString());
    trade.setCreatedAt(LocalDateTime.now());
    trade.setStatus(TradeStatus.CREATED);
    trade.setCreatedBy(getCurrentUser());

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
                getCurrentUser(),
                "UI"
        );

        if (eventType == TradeEventType.AMENDED ||
            eventType == TradeEventType.PRICED) {

            portfolioPositionService.updatePosition(updated);
        }

        return updated;
    }

    private String getCurrentUser() {
        return "FO_USER";
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
            // Use defaults
            return ValuationContext.builder()
                .trade(TradeContext.fromTrade(trade))
                .market(MarketContext.fromTrade(trade))
                .pricing(PricingContext.fromTrade(trade))
                .risk(RiskContext.fromTrade(trade))
                .accounting(AccountingContext.fromTrade(trade))
                .credit(CreditContext.fromTrade(trade))
                .audit(AuditContext.fromTrade(trade))
                .build();
        }

        // Use custom values from UI
        return ValuationContext.builder()
            .trade(TradeContext.fromTrade(trade))
            .market(MarketContext.fromTrade(
                trade,
                config.getMarketDataSet(),
                config.getPricingDate(),
                config.getCurveSet(),
                config.getFxScenario(),
                config.getVolatilitySurface()
            ))
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

    
}
