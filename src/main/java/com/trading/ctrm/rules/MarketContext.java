package com.trading.ctrm.rules;

import java.time.LocalDate;
import com.trading.ctrm.trade.Trade;

public class MarketContext {

    private final String marketDataSet;   // EOD / INTRADAY
    private final LocalDate pricingDate;
    private final String curveSet;
    private final String fxScenario;
    private final String volatilitySurface;

    public MarketContext(
            String marketDataSet,
            LocalDate pricingDate,
            String curveSet,
            String fxScenario,
            String volatilitySurface
    ) {
        this.marketDataSet = marketDataSet;
        this.pricingDate = pricingDate;
        this.curveSet = curveSet;
        this.fxScenario = fxScenario;
        this.volatilitySurface = volatilitySurface;
    }

    public String marketDataSet() { return marketDataSet; }
    public LocalDate pricingDate() { return pricingDate; }
    public String curveSet() { return curveSet; }
    public String fxScenario() { return fxScenario; }
    public String volatilitySurface() { return volatilitySurface; }

    public static MarketContext of(
            String marketDataSet,
            LocalDate pricingDate,
            String curveSet,
            String fxScenario,
            String volatilitySurface
    ) {
        return new MarketContext(
                marketDataSet,
                pricingDate,
                curveSet,
                fxScenario,
                volatilitySurface
        );
    }

    /**
     * Factory method to create MarketContext from a Trade entity with default market data settings
     */
    public static MarketContext fromTrade(Trade trade) {
        return new MarketContext(
                "EOD",
                LocalDate.now(),
                "USD_CURVES",
                "FX_EOD",
                trade.getInstrument().getInstrumentType().name() + "_VOL"
        );
    }

    /**
     * Factory method with configurable parameters from UI
     */
    public static MarketContext fromTrade(
            Trade trade,
            String marketDataSet,
            LocalDate pricingDate,
            String curveSet,
            String fxScenario,
            String volatilitySurface
    ) {
        return new MarketContext(
                marketDataSet != null ? marketDataSet : "EOD",
                pricingDate != null ? pricingDate : LocalDate.now(),
                curveSet != null ? curveSet : "USD_CURVES",
                fxScenario != null ? fxScenario : "FX_EOD",
                volatilitySurface != null ? volatilitySurface : trade.getInstrument().getInstrumentType().name() + "_VOL"
        );
    }
}