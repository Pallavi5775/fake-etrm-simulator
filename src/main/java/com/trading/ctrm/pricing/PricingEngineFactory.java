package com.trading.ctrm.pricing;

import org.springframework.stereotype.Component;

import com.trading.ctrm.instrument.InstrumentType;
import com.trading.ctrm.marketdata.MarketDataService;


@Component
public class PricingEngineFactory {

    private final MarketDataService marketDataService;

    public PricingEngineFactory(MarketDataService marketDataService) {
        this.marketDataService = marketDataService;
    }

    public PricingEngine getEngine(InstrumentType type) {
        return switch (type) {
            case POWER_FORWARD -> new PowerForwardPricingEngine(marketDataService);
            default -> throw new IllegalArgumentException("No pricing engine for: " + type);
        };
    }
}