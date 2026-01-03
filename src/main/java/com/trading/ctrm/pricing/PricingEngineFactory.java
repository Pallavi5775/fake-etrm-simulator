package com.trading.ctrm.pricing;

import org.springframework.stereotype.Component;

import com.trading.ctrm.instrument.InstrumentType;
import com.trading.ctrm.trade.ForwardCurveRepository;


@Component
public class PricingEngineFactory {

    private final ForwardCurveRepository forwardCurveRepository;

    public PricingEngineFactory(ForwardCurveRepository forwardCurveRepository) {
        this.forwardCurveRepository = forwardCurveRepository;
    }

    public PricingEngine getEngine(InstrumentType type) {
        return switch (type) {
            case POWER_FORWARD -> new PowerForwardPricingEngine(forwardCurveRepository);
            case OPTION -> new Black76PricingEngine();
            case RENEWABLE_PPA -> new RenewableForecastPricingEngine();
            case GAS_FORWARD, COMMODITY_SWAP, FREIGHT -> new DcfPricingEngine();
            default -> throw new IllegalArgumentException("No pricing engine for: " + type);
        };
    }
}