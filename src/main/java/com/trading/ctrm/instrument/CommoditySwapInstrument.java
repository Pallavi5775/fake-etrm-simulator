package com.trading.ctrm.instrument;
import jakarta.persistence.Entity;

@Entity
public class CommoditySwapInstrument extends Instrument {

    private String floatingPriceIndex;
    private java.math.BigDecimal fixedPrice;
    private java.time.LocalDate startDate;
    private java.time.LocalDate endDate;

    public CommoditySwapInstrument() {
        setInstrumentType(InstrumentType.COMMODITY_SWAP);
    }

    public String getFloatingPriceIndex() {
        return floatingPriceIndex;
    }

    public void setFloatingPriceIndex(String floatingPriceIndex) {
        this.floatingPriceIndex = floatingPriceIndex;
    }

    public java.math.BigDecimal getFixedPrice() {
        return fixedPrice;
    }

    public void setFixedPrice(java.math.BigDecimal fixedPrice) {
        this.fixedPrice = fixedPrice;
    }

    public java.time.LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(java.time.LocalDate startDate) {
        this.startDate = startDate;
    }

    public java.time.LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(java.time.LocalDate endDate) {
        this.endDate = endDate;
    }
}

// Series of fixed vs floating cashflows

// Monthly / quarterly settlements