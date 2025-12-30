package com.trading.ctrm.pricing;

import java.math.BigDecimal;

import com.trading.ctrm.instrument.Instrument;
import com.trading.ctrm.marketdata.MarketDataSnapshot;
import com.trading.ctrm.trade.Trade;

public interface PricingEngine {

    /**
     * Calculate MTM for a trade.
     *
     * @param trade      Trade instance
     * @param instrument Instrument definition
     * @param snapshot   Market data snapshot
     * @return MTM value
     */
    BigDecimal calculateMTM(
            Trade trade,
            Instrument instrument,
            MarketDataSnapshot snapshot
    );
}
