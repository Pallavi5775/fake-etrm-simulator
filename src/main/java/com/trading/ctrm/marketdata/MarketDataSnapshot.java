package com.trading.ctrm.marketdata;

import java.math.BigDecimal;
import java.util.Map;

public class MarketDataSnapshot {

    private final Map<String, BigDecimal> prices;

    public MarketDataSnapshot(Map<String, BigDecimal> prices) {
        this.prices = prices;
    }

    public BigDecimal getPrice(String instrumentCode) {
        return prices.get(instrumentCode);
    }
}
