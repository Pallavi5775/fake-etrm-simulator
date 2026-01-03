package com.trading.ctrm.lifestyle;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.trading.ctrm.common.BusinessException;
import com.trading.ctrm.instrument.Instrument;
import com.trading.ctrm.marketdata.MarketDataService;
import com.trading.ctrm.marketdata.MarketDataSnapshot;
import com.trading.ctrm.pricing.PricingEngine;
import com.trading.ctrm.pricing.PricingEngineFactory;
import com.trading.ctrm.pricing.ValuationHistory;
import com.trading.ctrm.pricing.ValuationHistoryRepository;
import com.trading.ctrm.pricing.ValuationResult;
import com.trading.ctrm.deals.DealTemplate;
import com.trading.ctrm.deals.DealTemplateRepository;
import com.trading.ctrm.trade.*;
import com.trading.ctrm.rules.ApprovalRouting;
import com.trading.ctrm.rules.ApprovalRoutingService;
import com.trading.ctrm.rules.ApprovalRuleEngine;
import com.trading.ctrm.rules.ApprovalExecutionService;
import com.trading.ctrm.rules.ApprovalRule;
import com.trading.ctrm.rules.TradeContext;

import jakarta.transaction.Transactional;

import com.trading.ctrm.lifestyle.handler.TradeEventHandlerRegistry;

/**
 * Core lifecycle engine – config-driven, event-based (TPM)
 */
@Service
public class TradeLifecycleEngine {

    private final LifecycleRuleRepository ruleRepository;
    private final TradeEventRepository tradeEventRepository;
    private final TradeRepository tradeRepository;
    private final TradeEventHandlerRegistry handlerRegistry;

    // 🔥 Pricing & Market Data
    private final PricingEngineFactory pricingEngineFactory;
    private final MarketDataService marketDataService;
    private final DealTemplateRepository templateRepository;
    private final ValuationHistoryRepository valuationHistoryRepository;
    private final ApprovalRoutingService approvalRoutingService;
    private final ApprovalRuleEngine approvalRuleEngine;
    private final ApprovalExecutionService approvalExecutionService;

    // ✅ EXPLICIT constructor
    public TradeLifecycleEngine(
            LifecycleRuleRepository ruleRepository,
            TradeEventRepository tradeEventRepository,
            TradeRepository tradeRepository,
            TradeEventHandlerRegistry handlerRegistry,
            PricingEngineFactory pricingEngineFactory,
            MarketDataService marketDataService,
            DealTemplateRepository templateRepository,
            ValuationHistoryRepository valuationHistoryRepository,
            ApprovalRoutingService approvalRoutingService,
            ApprovalRuleEngine approvalRuleEngine,
            ApprovalExecutionService approvalExecutionService) {

        this.ruleRepository = ruleRepository;
        this.tradeEventRepository = tradeEventRepository;
        this.tradeRepository = tradeRepository;
        this.handlerRegistry = handlerRegistry;
        this.pricingEngineFactory = pricingEngineFactory;
        this.marketDataService = marketDataService;
        this.templateRepository = templateRepository;
        this.valuationHistoryRepository = valuationHistoryRepository;
        this.approvalRoutingService = approvalRoutingService;
        this.approvalRuleEngine = approvalRuleEngine;
        this.approvalExecutionService = approvalExecutionService;
    }

    /**
     * Apply a lifecycle event to a trade
     */
    @Transactional
    public Trade applyEvent(
            Trade trade,
            TradeEventType eventType,
            String triggeredBy,
            String source) {

        // 1️⃣ Resolve lifecycle rule (USDR)
        LifecycleRule rule = ruleRepository
                .findRule(trade.getStatus(), eventType, trade.getPortfolio())
                .orElseThrow(() ->
                    new DynamicBusinessException(
                        RejectionReason.NO_RULE_FOUND,
                        "No lifecycle rule configured for "
                        + trade.getStatus() + " → " + eventType
                    )
                );

        // 2️⃣ Validate max occurrence
        validateOccurrence(trade, rule);

        // 3️⃣ Apply status transition based on rule
        System.out.println("🔄 Applying lifecycle event: " + eventType);
        System.out.println("   Current status: " + trade.getStatus());
        System.out.println("   Rule to_status: " + rule.getToStatus());
        System.out.println("   Rule auto_approve: " + rule.isAutoApprove());
        
        if (rule.isAutoApprove()) {
            // For certain events like CANCELLED, SETTLED, directly apply to_status
            if (eventType == TradeEventType.CANCELLED || eventType == TradeEventType.SETTLED) {
                System.out.println("   ✅ Directly applying to_status: " + rule.getToStatus());
                trade.setStatus(rule.getToStatus());
            } else {
                // For other events, use MTM-based approval logic
                System.out.println("   📊 Using MTM-based approval");
                evaluateMTMBasedApproval(trade);
            }
        } else {
            System.out.println("   ⏸️ Setting to PENDING_APPROVAL (no auto-approve)");
            trade.setStatus(TradeStatus.PENDING_APPROVAL);
        }
        
        System.out.println("   Final status: " + trade.getStatus());

        // 4️⃣ Persist trade state
        Trade savedTrade = tradeRepository.save(trade);

        // 5️⃣ Persist audit event
        TradeEvent event = TradeEvent.of(
                savedTrade,
                eventType,
                triggeredBy,
                source
        );
        tradeEventRepository.save(event);

        // 6️⃣ Execute side-effects ONLY if approved
        if (savedTrade.getStatus() != TradeStatus.PENDING_APPROVAL) {
            handlerRegistry.handle(eventType, savedTrade);
        }

        // 7️⃣ Return updated trade
        return savedTrade;
    }

    /**
     * MTM-based approval logic (Pricing + USDR)
     */
    private void evaluateMTMBasedApproval(Trade trade) {

        DealTemplate template = templateRepository
                .findByInstrument(trade.getInstrument())
                .orElseThrow(() ->
                    new BusinessException(
                        "No deal template found for instrument "
                        + trade.getInstrument().getInstrumentCode()
                    )
                );

        Instrument instrument = trade.getInstrument();

        // Build ValuationContext for pricing
        com.trading.ctrm.rules.ValuationContext valuationContext =
            com.trading.ctrm.rules.ValuationContext.builder()
                .trade(com.trading.ctrm.rules.TradeContext.fromTrade(trade))
                .market(com.trading.ctrm.rules.MarketContext.fromTrade(trade))
                .pricing(com.trading.ctrm.rules.PricingContext.fromTrade(trade))
                .risk(com.trading.ctrm.rules.RiskContext.fromTrade(trade))
                .accounting(com.trading.ctrm.rules.AccountingContext.fromTrade(trade))
                .credit(com.trading.ctrm.rules.CreditContext.fromTrade(trade))
                .audit(com.trading.ctrm.rules.AuditContext.fromTrade(trade))
                .build();

        PricingEngine engine =
                pricingEngineFactory.getEngine(
                        instrument.getInstrumentType()
                );

        // Get comprehensive valuation result
        ValuationResult result = engine.price(trade, instrument, valuationContext);

        // ✅ Store MTM on Trade (for UI)
        trade.setMtm(result.getMtmTotal());

        // ✅ Persist valuation history
        valuationHistoryRepository.save(
                new ValuationHistory(
                        trade,
                        result.getMtmTotal(),
                        LocalDate.now()
                )
        );

        if (result.getMtmTotal().abs().compareTo(template.getMtmApprovalThreshold()) > 0) {
            trade.setStatus(TradeStatus.PENDING_APPROVAL);
            trade.setPendingApprovalRole("RISK");
        } else {
            trade.setStatus(TradeStatus.APPROVED);
        }
    }

    /**
     * Centralized approved status resolution
     */
    private TradeStatus ruleSafeApprovedStatus(Trade trade) {
        return TradeStatus.CONFIRMED;
    }

    private void validateOccurrence(Trade trade, LifecycleRule rule) {

        long count = tradeEventRepository
                .countByTradeAndEventType(trade, TradeEventType.AMENDED);

        if (count >= rule.getMaxOccurrence()) {
            throw new BusinessException(
                    "Event " + rule.getEventType() +
                    " exceeded max occurrence: " + rule.getMaxOccurrence()
            );
        }
    }

    /**
     * Resolve next trade status WITHOUT persisting anything.
     * Used by Rule Simulator / What-if analysis.
     */
    public TradeStatus resolveNextStatus(
            TradeStatus fromStatus,
            TradeEventType eventType,
            String desk) {

        LifecycleRule rule = ruleRepository
                .findRule(fromStatus, eventType, desk)
                .orElseThrow(() ->
                    new BusinessException(
                        "No lifecycle rule configured for "
                        + fromStatus + " → " + eventType
                        + " for desk " + desk
                    )
                );

        return rule.getToStatus();
    }

    @Transactional
    public void handleEvent(
            Long tradeId,
            String event,
            Map<String, Object> payload
    ) {

        Trade trade = tradeRepository.findByIdForUpdate(tradeId)
                .orElseThrow();

        ExecutionContext ctx = new ExecutionContext();
        ctx.setTrade(trade);
        ctx.setOriginalStatus(trade.getStatus());
        ctx.setMode(ExecutionMode.RUNTIME);

        if (payload != null) {
            ctx.getAttributes().putAll(payload);
        }

        try {
            List<LifecycleRule> rules =
                ruleRepository.findEligibleRules(trade.getStatus(), event);

            for (LifecycleRule rule : rules) {
                rule.execute(ctx);
            }

            tradeRepository.save(trade);

        } catch (Exception ex) {
            trade.setStatus(ctx.getOriginalStatus());
            tradeRepository.save(trade);
            throw ex;
        }
    }

    @Transactional
    public Trade rejectTrade(
            String tradeId,
            String rejectionReason,
            String rejectedBy) {

        Trade trade = findTrade(tradeId);

        if (trade.getStatus() != TradeStatus.PENDING_APPROVAL) {
            throw new BusinessException(
                "Only pending trades can be rejected"
            );
        }

        // ===== APPROVAL RULE EVALUATION FOR REJECTION =====
        TradeContext ctx = TradeContext.fromTrade(trade);

        System.out.println("🔍 Evaluating approval rules for trade rejection: " + trade.getTradeId());
        System.out.println("   Quantity: " + ctx.quantity());
        System.out.println("   Counterparty: " + ctx.counterparty());
        System.out.println("   Portfolio: " + ctx.portfolio());
        System.out.println("   InstrumentType: " + ctx.instrumentType());

        Optional<ApprovalRule> matchedRule =
            approvalRuleEngine.evaluate(ctx, "TRADE_REJECT");

        System.out.println("   Rule matched for rejection: " + matchedRule.isPresent());

        if (matchedRule.isPresent()) {
            // Rejection requires approval - create pending approval for rejection
            ApprovalRule rule = matchedRule.get();

            approvalExecutionService.createPendingApproval(
                ctx,
                rule,
                "TRADE_REJECT"
            );

            // Update trade with rejection approval details
            trade.setMatchedRuleId(rule.getRuleId());
            trade.setCurrentApprovalLevel(1);
            trade.setPendingApprovalRole(
                rule.getRouting().stream()
                    .filter(r -> r.getApprovalLevel() == 1)
                    .findFirst()
                    .map(ApprovalRouting::getApprovalRole)
                    .orElse("SENIOR_TRADER")
            );
            // Status remains PENDING_APPROVAL but now for rejection approval

            Trade saved = tradeRepository.save(trade);

            TradeEvent event = TradeEvent.of(
                    saved,
                    TradeEventType.REJECTED,
                    rejectedBy,
                    "APPROVAL_UI - Pending rejection approval"
            );
            tradeEventRepository.save(event);

            return saved;
        } else {
            // No approval rule for rejection - reject immediately
            trade.setStatus(TradeStatus.REJECTED);
            trade.setPendingApprovalRole(null);

            Trade saved = tradeRepository.save(trade);

            TradeEvent event = TradeEvent.of(
                    saved,
                    TradeEventType.REJECTED,
                    rejectedBy,
                    "APPROVAL_UI"
            );
            tradeEventRepository.save(event);

            return saved;
        }
    }

    @Transactional
    public Trade approveTrade(
            String tradeId,
            String approverRole,
            String approvedBy) {

        Trade trade = findTrade(tradeId);

        if (trade.getStatus() != TradeStatus.PENDING_APPROVAL) {
            throw new BusinessException(
                "Trade is not pending approval: " + trade.getStatus()
            );
        }

        // Verify the approver has the required role
        if (trade.getPendingApprovalRole() != null && 
            !approverRole.equals(trade.getPendingApprovalRole())) {
            throw new BusinessException(
                "Approval requires role: " + trade.getPendingApprovalRole() + 
                ", but user has role: " + approverRole
            );
        }

        // Check if there are more approval levels
        if (trade.getMatchedRuleId() != null && trade.getCurrentApprovalLevel() != null) {
            Optional<ApprovalRouting> nextLevel = approvalRoutingService.getNextLevel(
                trade.getMatchedRuleId(),
                trade.getCurrentApprovalLevel()
            );

            if (nextLevel.isPresent()) {
                // Move to next approval level
                trade.setCurrentApprovalLevel(nextLevel.get().getApprovalLevel());
                trade.setPendingApprovalRole(nextLevel.get().getApprovalRole());
                // Status remains PENDING_APPROVAL
            } else {
                // No more levels - fully approved
                trade.setStatus(TradeStatus.APPROVED);
                trade.setPendingApprovalRole(null);
                trade.setCurrentApprovalLevel(null);
            }
        } else {
            // No rule matched or no levels - approve immediately
            trade.setStatus(TradeStatus.APPROVED);
            trade.setPendingApprovalRole(null);
        }

        Trade saved = tradeRepository.save(trade);

        TradeEvent event = TradeEvent.of(
                saved,
                TradeEventType.APPROVED,
                approvedBy,
                "APPROVAL_UI"
        );
        tradeEventRepository.save(event);

        handlerRegistry.handle(TradeEventType.APPROVED, saved);

        return saved;
    }

    /**
     * Smart lookup - handles both numeric IDs and business trade IDs
     */
    private Trade findTrade(String tradeId) {
        // Try parsing as Long first (numeric database ID)
        try {
            Long id = Long.parseLong(tradeId);
            return tradeRepository.findById(id)
                    .orElseThrow(() -> new BusinessException("Trade not found with id: " + tradeId));
        } catch (NumberFormatException e) {
            // Not a number, lookup by business trade ID
            return tradeRepository.findByTradeId(tradeId)
                    .orElseThrow(() -> new BusinessException("Trade not found: " + tradeId));
        }
    }
}
