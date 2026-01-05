package com.trading.ctrm.instrument;

import jakarta.persistence.Entity;

@Entity
public class FreightInstrument extends Instrument {

    public FreightInstrument() {
        setInstrumentType(InstrumentType.FREIGHT);
    }

    // Freight-specific fields can be added here as needed
    // For now, using base Instrument fields (commodity, currency, unit)
}