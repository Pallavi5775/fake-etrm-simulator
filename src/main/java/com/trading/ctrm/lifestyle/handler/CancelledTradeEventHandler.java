package com.trading.ctrm.lifestyle.handler;

import org.springframework.stereotype.Component;
import com.trading.ctrm.trade.Trade;

@Component
public class CancelledTradeEventHandler implements TradeEventHandler {

    @Override
    public void handle(Trade trade) {

        // Typical CTRM actions:
        // - Reverse provisional positions
        // - Notify risk & compliance
        // - Freeze trade

        System.out.println(
            "Trade CANCELLED: " + trade.getTradeId()
        );
    }
}
