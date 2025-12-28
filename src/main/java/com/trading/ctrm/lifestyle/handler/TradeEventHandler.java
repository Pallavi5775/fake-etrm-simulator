package com.trading.ctrm.lifestyle.handler;

import com.trading.ctrm.trade.Trade;

public interface TradeEventHandler {
    void handle(Trade trade);
}
