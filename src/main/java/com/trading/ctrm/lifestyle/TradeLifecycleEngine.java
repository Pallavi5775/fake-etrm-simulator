package com.trading.ctrm.lifestyle;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.trading.ctrm.common.BusinessException;

import com.trading.ctrm.trade.Trade;
import com.trading.ctrm.trade.TradeEvent;
import com.trading.ctrm.trade.TradeEventRepository;
import com.trading.ctrm.trade.TradeEventType;
import com.trading.ctrm.trade.TradeRepository;
import com.trading.ctrm.trade.TradeStatus;

import jakarta.transaction.Transactional;

import com.trading.ctrm.lifestyle.handler.TradeEventHandlerRegistry;

/**
 * Core lifecycle engine – config-driven, event-based
 */
@Service
public class TradeLifecycleEngine {

    private final LifecycleRuleRepository ruleRepository;
    private final TradeEventRepository tradeEventRepository;
    private final TradeRepository tradeRepository;
    private final TradeEventHandlerRegistry handlerRegistry;

    // ✅ EXPLICIT constructor (no Lombok)
    public TradeLifecycleEngine(
            LifecycleRuleRepository ruleRepository,
            TradeEventRepository tradeEventRepository,
            TradeRepository tradeRepository,
            TradeEventHandlerRegistry handlerRegistry) {

        this.ruleRepository = ruleRepository;
        this.tradeEventRepository = tradeEventRepository;
        this.tradeRepository = tradeRepository;
        this.handlerRegistry = handlerRegistry;
    }

/**
 * Apply a lifecycle event to a trade
 */
public Trade applyEvent(
        Trade trade,
        TradeEventType eventType,
        String triggeredBy,
        String source) {

    // 1️⃣ Resolve lifecycle rule
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

    // 3️⃣ Apply transition with approval logic
    if (rule.isAutoApprove()) {
        trade.setStatus(rule.getToStatus());
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

    // 6️⃣ Execute side-effects ONLY if auto-approved
    if (rule.isAutoApprove()) {
        handlerRegistry.handle(eventType, savedTrade);
    }

    // 7️⃣ Return updated trade
    return savedTrade;
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

        // only return next status (NO SIDE EFFECTS)
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

    // ✅ attach payload safely
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
public Trade approveTrade(Long tradeId, String approverRole, String approvedBy) {

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

    // ✅ Finalize approval
    trade.setStatus(TradeStatus.CONFIRMED);
    trade.setPendingApprovalRole(null);
    Trade saved = tradeRepository.save(trade);

    // ✅ Audit event
    TradeEvent event = TradeEvent.of(
            saved,
            TradeEventType.APPROVED,
            approvedBy,
            "APPROVAL_UI"
    );
    tradeEventRepository.save(event);

    // ✅ Execute deferred side-effects
    handlerRegistry.handle(TradeEventType.APPROVED, saved);

    return saved;
}



}
