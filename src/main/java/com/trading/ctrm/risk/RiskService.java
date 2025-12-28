package com.trading.ctrm.risk;

import org.springframework.stereotype.Service;

import com.trading.ctrm.trade.Trade;

@Service
public class RiskService {

    public double exposure(Trade trade, double marketPrice) {
        return trade.getQuantity() * marketPrice;
    }

    public double creditExposure(
            Trade trade, double marketPrice, double ratingFactor) {
        return exposure(trade, marketPrice) * ratingFactor;
    }
}

