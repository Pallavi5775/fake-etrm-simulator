package com.trading.ctrm.instrument;

import jakarta.persistence.Entity;

@Entity
public class RenewablePPAInstrument extends Instrument {

    private String technology;        // WIND / SOLAR
    private String forecastCurve;     // WIND_FORECAST_2025
    private String settlementType;    // PHYSICAL / FINANCIAL

    public RenewablePPAInstrument() {
        setInstrumentType(InstrumentType.RENEWABLE_PPA);
    }

    public String getTechnology() {
        return technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    public String getForecastCurve() {
        return forecastCurve;
    }

    public void setForecastCurve(String forecastCurve) {
        this.forecastCurve = forecastCurve;
    }

    public String getSettlementType() {
        return settlementType;
    }

    public void setSettlementType(String settlementType) {
        this.settlementType = settlementType;
    }
}


// Variable generation

// Forecast-driven

// Physical or financial
