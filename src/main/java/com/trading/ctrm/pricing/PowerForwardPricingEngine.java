package com.trading.ctrm.pricing;

import java.math.BigDecimal;

import com.trading.ctrm.instrument.Instrument;
import com.trading.ctrm.marketdata.MarketDataSnapshot;
import com.trading.ctrm.trade.Trade;



public class PowerForwardPricingEngine implements PricingEngine {

    @Override
    public BigDecimal calculateMTM(
            Trade trade,
            Instrument instrument,
            MarketDataSnapshot snapshot
    ) {

        BigDecimal marketPrice =
                snapshot.getPrice(instrument.getInstrumentCode());

        if (marketPrice == null) {
            throw new IllegalStateException(
                    "No market price found for instrument: " +
                    instrument.getInstrumentCode()
            );
        }

        // MTM = (Market Price - Trade Price) × Quantity
        return marketPrice
                .subtract(trade.getPrice())
                .multiply(trade.getQuantity());
    }
}




// This gives you:

// Real MTM

// Deterministic behavior

// Something approvals can consume