package com.trading.ctrm.lifestyle.handler;

import org.springframework.stereotype.Component;
import com.trading.ctrm.trade.Trade;

@Component
public class InvoicedTradeEventHandler implements TradeEventHandler {

    @Override
    public void handle(Trade trade) {

        // Real-world steps:
        // - Calculate invoice amount
        // - Generate invoice document
        // - Send to counterparty
        // - Store invoice reference

        System.out.println(
            "Invoice generated for trade: " +
            trade.getTradeId()
        );
    }
}

