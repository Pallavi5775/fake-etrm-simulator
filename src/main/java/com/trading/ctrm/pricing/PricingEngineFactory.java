package com.trading.ctrm.pricing;

import org.springframework.stereotype.Component;

import com.trading.ctrm.instrument.InstrumentType;


@Component
public class PricingEngineFactory {

    public PricingEngine getEngine(InstrumentType type) {
        return switch (type) {
            case POWER_FORWARD -> new PowerForwardPricingEngine();
            default -> throw new IllegalArgumentException("No pricing engine");
        };
    }
}