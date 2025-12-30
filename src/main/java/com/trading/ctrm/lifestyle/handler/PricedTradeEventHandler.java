package com.trading.ctrm.lifestyle.handler;

import org.springframework.stereotype.Component;
import com.trading.ctrm.trade.Trade;

@Component
public class PricedTradeEventHandler implements TradeEventHandler {

    @Override
    public void handle(Trade trade) {

        // Real CTRM logic:
        // - Fetch forward curve
        // - Compute price / MTM
        // - Store valuation snapshot

        System.out.println(
            "Trade PRICED using forward curve: " +
            trade.getInstrument().getInstrumentCode()
        );
    }
}

