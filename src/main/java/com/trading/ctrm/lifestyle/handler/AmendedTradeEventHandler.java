package com.trading.ctrm.lifestyle.handler;

import org.springframework.stereotype.Component;
import com.trading.ctrm.trade.Trade;

@Component
public class AmendedTradeEventHandler implements TradeEventHandler {

    @Override
    public void handle(Trade trade) {

        // Real-world behavior:
        // - Increment trade version
        // - Persist before/after snapshot
        // - Notify risk & ops

        System.out.println(
            "Trade AMENDED (version increment): " +
            trade.getTradeId()
        );
    }
}

