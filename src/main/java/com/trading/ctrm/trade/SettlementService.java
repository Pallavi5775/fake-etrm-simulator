package com.trading.ctrm.trade;

import org.springframework.stereotype.Service;


@Service
public class SettlementService {

    public boolean settleTrade(Trade trade) {
        return trade.getStatus() == TradeStatus.INVOICED;
    }
}

