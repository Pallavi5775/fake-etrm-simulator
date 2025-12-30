package com.trading.ctrm.instrument;

import java.time.LocalDate;

import jakarta.persistence.Entity;

@Entity
public class PowerForwardInstrument extends Instrument {

    private LocalDate startDate;
    private LocalDate endDate;

    public PowerForwardInstrument() {
        setInstrumentType(InstrumentType.POWER_FORWARD);
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}


// This gives you:
// Fixed delivery
// Simple MTM
// Baseline pricing