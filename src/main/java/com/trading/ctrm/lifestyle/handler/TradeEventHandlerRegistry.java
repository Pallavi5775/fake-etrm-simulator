package com.trading.ctrm.lifestyle.handler;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.trading.ctrm.trade.Trade;
import com.trading.ctrm.trade.TradeEventType;

@Component
public class TradeEventHandlerRegistry {

    private final Map<TradeEventType, TradeEventHandler> handlers =
            new EnumMap<>(TradeEventType.class);

    public TradeEventHandlerRegistry(List<TradeEventHandler> handlerList) {

        for (TradeEventHandler handler : handlerList) {

            if (handler instanceof CreatedTradeEventHandler) {
                handlers.put(TradeEventType.CREATED, handler);
            }
            if (handler instanceof PricedTradeEventHandler) {
                handlers.put(TradeEventType.PRICED, handler);
            }
            if (handler instanceof AmendedTradeEventHandler) {
                handlers.put(TradeEventType.AMENDED, handler);
            }
            if (handler instanceof DeliveredTradeEventHandler) {
                handlers.put(TradeEventType.DELIVERED, handler);
            }
            if (handler instanceof InvoicedTradeEventHandler) {
                handlers.put(TradeEventType.INVOICED, handler);
            }
            if (handler instanceof SettledTradeEventHandler) {
                handlers.put(TradeEventType.SETTLED, handler);
            }
            if (handler instanceof CancelledTradeEventHandler) {
                handlers.put(TradeEventType.CANCELLED, handler);
            }
        }
    }

    public void handle(TradeEventType eventType, Trade trade) {

        TradeEventHandler handler = handlers.get(eventType);

        if (handler != null) {
            handler.handle(trade);
        }
    }
}
