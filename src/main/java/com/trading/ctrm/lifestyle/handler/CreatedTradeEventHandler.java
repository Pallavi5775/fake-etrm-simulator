package com.trading.ctrm.lifestyle.handler;

import org.springframework.stereotype.Component;
import com.trading.ctrm.trade.Trade;

@Component
public class CreatedTradeEventHandler implements TradeEventHandler {

    @Override
    public void handle(Trade trade) {
        // Intentionally minimal
        // Booking is a legal act, not a financial one

        // Typical real-world actions:
        // - Notify downstream systems
        // - Index trade for search
        // - Publish booking event

        System.out.println(
            "Trade CREATED: " + trade.getTradeId()
        );
    }
}

