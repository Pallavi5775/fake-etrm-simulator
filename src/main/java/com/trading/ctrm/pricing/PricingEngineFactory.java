package com.trading.ctrm.pricing;

import org.springframework.stereotype.Component;

import com.trading.ctrm.instrument.Instrument;
import com.trading.ctrm.instrument.InstrumentType;
import com.trading.ctrm.instrument.CommodityOptionInstrument;
import com.trading.ctrm.rules.MarketContext;


@Component
public class PricingEngineFactory {

    private final MarketContext marketContext;

    public PricingEngineFactory(MarketContext marketContext) {
        this.marketContext = marketContext;
    }

    public PricingEngine getEngine(InstrumentType type) {
        return switch (type) {
            case POWER_FORWARD -> new PowerForwardPricingEngine(marketContext);
            case OPTION -> new Black76PricingEngine(); // Default to Black76 for backward compatibility
            case RENEWABLE_PPA -> new RenewableForecastPricingEngine();
            case GAS_FORWARD, COMMODITY_SWAP, FREIGHT -> new DcfPricingEngine();
            default -> throw new IllegalArgumentException("No pricing engine for: " + type);
        };
    }

    public PricingEngine getEngine(Instrument instrument) {
        InstrumentType type = instrument.getInstrumentType();
        return switch (type) {
            case POWER_FORWARD -> new PowerForwardPricingEngine(marketContext);
            case OPTION -> {
                // For options, check underlying type to choose pricing model
                if (instrument instanceof CommodityOptionInstrument) {
                    CommodityOptionInstrument option = (CommodityOptionInstrument) instrument;
                    String underlyingType = option.getUnderlyingType();
                    if ("FUTURES".equalsIgnoreCase(underlyingType)) {
                        yield new BlackPricingEngine();
                    }
                }
                // Default to Black76 for forwards or unspecified
                yield new Black76PricingEngine();
            }
            case RENEWABLE_PPA -> new RenewableForecastPricingEngine();
            case GAS_FORWARD, COMMODITY_SWAP, FREIGHT -> new DcfPricingEngine();
            default -> throw new IllegalArgumentException("No pricing engine for: " + type);
        };
    }
}