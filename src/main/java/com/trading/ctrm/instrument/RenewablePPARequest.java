package com.trading.ctrm.instrument;

public class RenewablePPARequest {
    
    private String instrumentCode;
    private String commodity;
    private String currency;
    private String unit;
    private String technology;        // WIND / SOLAR
    private String forecastCurve;     // WIND_FORECAST_2025
    private String settlementType;    // PHYSICAL / FINANCIAL

    public String getInstrumentCode() {
        return instrumentCode;
    }

    public void setInstrumentCode(String instrumentCode) {
        this.instrumentCode = instrumentCode;
    }

    public String getCommodity() {
        return commodity;
    }

    public void setCommodity(String commodity) {
        this.commodity = commodity;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
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
