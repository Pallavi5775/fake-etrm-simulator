package com.trading.ctrm.lifestyle;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.trading.ctrm.common.BusinessException;
import com.trading.ctrm.instrument.Instrument;
import com.trading.ctrm.marketdata.MarketDataService;
import com.trading.ctrm.marketdata.MarketDataSnapshot;
import com.trading.ctrm.pricing.PricingEngine;
import com.trading.ctrm.pricing.PricingEngineFactory;
import com.trading.ctrm.pricing.ValuationHistory;
import com.trading.ctrm.pricing.ValuationHistoryRepository;
import com.trading.ctrm.deals.DealTemplate;
import com.trading.ctrm.deals.DealTemplateRepository;
import com.trading.ctrm.trade.*;

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

    // ✅ EXPLICIT constructor
    public TradeLifecycleEngine(
            LifecycleRuleRepository ruleRepository,
            TradeEventRepository tradeEventRepository,
            TradeRepository tradeRepository,
            TradeEventHandlerRegistry handlerRegistry,
            PricingEngineFactory pricingEngineFactory,
            MarketDataService marketDataService,
            DealTemplateRepository templateRepository,
            ValuationHistoryRepository valuationHistoryRepository) {

        this.ruleRepository = ruleRepository;
        this.tradeEventRepository = tradeEventRepository;
        this.tradeRepository = tradeRepository;
        this.handlerRegistry = handlerRegistry;
        this.pricingEngineFactory = pricingEngineFactory;
        this.marketDataService = marketDataService;
        this.templateRepository = templateRepository;
        this.valuationHistoryRepository = valuationHistoryRepository;
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

        // 3️⃣ Pricing + MTM-based approval (ONLY when rule allows auto-approve)
        if (rule.isAutoApprove()) {
            evaluateMTMBasedApproval(trade);
        } else {
            trade.setStatus(TradeStatus.PENDING_APPROVAL);
        }

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

        MarketDataSnapshot snapshot = marketDataService.loadSnapshot();
        Instrument instrument = trade.getInstrument();

        PricingEngine engine =
                pricingEngineFactory.getEngine(
                        instrument.getInstrumentType()
                );

        BigDecimal mtm =
                engine.calculateMTM(trade, instrument, snapshot);

        // ✅ Store MTM on Trade (for UI)
        trade.setMtm(mtm);

        // ✅ Persist valuation history (THIS IS THE CHANGE YOU ASKED FOR)
        valuationHistoryRepository.save(
                new ValuationHistory(
                        trade,
                        mtm,
                        LocalDate.now()
                )
        );

        if (mtm.abs().compareTo(template.getMtmApprovalThreshold()) > 0) {
            trade.setStatus(TradeStatus.PENDING_APPROVAL);
            trade.setPendingApprovalRole("RISK");
        } else {
            trade.setStatus(TradeStatus.CONFIRMED);
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
            Long tradeId,
            String rejectionReason,
            String rejectedBy) {

        Trade trade = tradeRepository.findByIdForUpdate(tradeId)
                .orElseThrow(() ->
                    new BusinessException("Trade not found: " + tradeId)
                );

        if (trade.getStatus() != TradeStatus.PENDING_APPROVAL) {
            throw new BusinessException(
                "Only pending trades can be rejected"
            );
        }

        trade.setStatus(TradeStatus.REJECTED);
        trade.setPendingApprovalRole(null);

        Trade saved = tradeRepository.save(trade);

        TradeEvent event = TradeEvent.of(
                saved,
                TradeEventType.REJECTED,
                rejectedBy,
                rejectionReason
        );
        tradeEventRepository.save(event);

        return saved;
    }

    @Transactional
    public Trade approveTrade(
            Long tradeId,
            String approverRole,
            String approvedBy) {

        Trade trade = tradeRepository.findByIdForUpdate(tradeId)
                .orElseThrow(() ->
                    new BusinessException("Trade not found: " + tradeId)
                );

        if (trade.getStatus() != TradeStatus.PENDING_APPROVAL) {
            throw new BusinessException(
                "Trade is not pending approval: " + trade.getStatus()
            );
        }

        if (!approverRole.equals(trade.getPendingApprovalRole())) {
            throw new BusinessException(
                "Approval requires role: " + trade.getPendingApprovalRole()
            );
        }

        trade.setStatus(TradeStatus.CONFIRMED);
        trade.setPendingApprovalRole(null);
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
}
