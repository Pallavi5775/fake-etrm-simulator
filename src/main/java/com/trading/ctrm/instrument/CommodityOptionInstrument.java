package com.trading.ctrm.instrument;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Entity;

@Entity
public class CommodityOptionInstrument extends Instrument {

    private java.math.BigDecimal strikePrice;
    private java.time.LocalDate expiryDate;
    private String optionType; // CALL / PUT

    public CommodityOptionInstrument() {
        setInstrumentType(InstrumentType.OPTION);
    }

    public java.math.BigDecimal getStrikePrice() {
        return strikePrice;
    }

    public void setStrikePrice(java.math.BigDecimal strikePrice) {
        this.strikePrice = strikePrice;
    }

    public java.time.LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(java.time.LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getOptionType() {
        return optionType;
    }

    public void setOptionType(String optionType) {
        this.optionType = optionType;
    }
}



// Right, not obligation

// Volatility-based pricing
// Pricing will later use volatility surfaces.