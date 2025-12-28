package com.trading.ctrm.trade;

import org.springframework.stereotype.Service;
import com.trading.ctrm.lifestyle.TradeLifecycleValidator;

@Service
public class TradeLifecycleService {

    private final TradeRepository tradeRepo;
    private final TradeEventRepository tradeEventRepo;

    public TradeLifecycleService(
            TradeRepository tradeRepo,
            TradeEventRepository tradeEventRepo) {
        this.tradeRepo = tradeRepo;
        this.tradeEventRepo = tradeEventRepo;
    }

    private TradeStatus mapEventToStatus(TradeEventType eventType) {
        return switch (eventType) {
            case CREATED   -> TradeStatus.CREATED;
            case PRICED    -> TradeStatus.PRICED;
            case AMENDED   -> TradeStatus.PRICED;   // AMEND does not advance
            case DELIVERED -> TradeStatus.DELIVERED;
            case INVOICED  -> TradeStatus.INVOICED;
            case SETTLED   -> TradeStatus.SETTLED;
            case CANCELLED -> TradeStatus.CANCELLED;
            case REJECTED  -> TradeStatus.REJECTED;
            case APPROVED  -> TradeStatus.APPROVED;
        };
    }

    public Trade applyEvent(
            String tradeId,
            TradeEventType eventType,
            String triggeredBy,
            String source) {

        // ---- Load trade ----
        Trade trade = tradeRepo.findByTradeId(tradeId)
                .orElseThrow(() -> new RuntimeException("Trade not found: " + tradeId));

        // ---- Validate lifecycle transition ----
        TradeLifecycleValidator.validate(trade.getStatus(), eventType);

        // ---- RULE: AMEND allowed only once after PRICED ----
        if (eventType == TradeEventType.AMENDED) {

            if (trade.getStatus() != TradeStatus.PRICED) {
                throw new IllegalStateException(
                        "AMEND is allowed only after PRICED"
                );
            }

            long amendCount = tradeEventRepo
                    .countByTradeAndEventType(trade, TradeEventType.AMENDED);

            if (amendCount >= 1) {
                throw new IllegalStateException(
                        "Trade can be amended only once after pricing"
                );
            }
        }

        // ---- Apply state transition ----
        trade.setStatus(mapEventToStatus(eventType));
        tradeRepo.save(trade);

        // ---- Persist immutable lifecycle event ----
        TradeEvent event = TradeEvent.of(
                trade,
                eventType,
                triggeredBy,
                source
        );

        tradeEventRepo.save(event);

        return trade;
    }
}
