package com.trading.ctrm.lifestyle.handler;

import org.springframework.stereotype.Component;
import com.trading.ctrm.trade.Trade;

@Component
public class SettledTradeEventHandler implements TradeEventHandler {

    @Override
    public void handle(Trade trade) {

        // Real-world steps:
        // - Confirm bank settlement
        // - Post accounting entries
        // - Close PnL

        System.out.println(
            "Trade SETTLED. Cash movement completed."
        );
    }
}
