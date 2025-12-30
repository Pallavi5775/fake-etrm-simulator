package com.trading.ctrm.instrument;

import java.time.LocalDate;

import jakarta.persistence.Entity;

@Entity
public class GasForwardInstrument extends Instrument {

    private LocalDate deliveryDate;

    public GasForwardInstrument() {
        setInstrumentType(InstrumentType.GAS_FORWARD);
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
}


// Same mechanics as power, but:

// Different curves

// Different units

// Different settlement logic later

