package com.trading.ctrm.instrument;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CommodityOptionRequest {
        public String getCurrency() {
            return currency;
        }
    
    private String instrumentCode;
    private Long commodityId;
    private String currency;
    private String unit;
    private BigDecimal strikePrice;
    private LocalDate expiryDate;
    private String optionType; // CALL or PUT

    public String getInstrumentCode() {
        return instrumentCode;
    }

    public void setInstrumentCode(String instrumentCode) {
        this.instrumentCode = instrumentCode;
    }

    /**
     * Deprecated: use getCommodityId instead
     */
    public String getCommodity() {
        return null;
    }

    /**
     * Deprecated: use setCommodityId instead
     */
    public void setCommodity(String commodity) {
        // no-op
    }

    public Long getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(Long commodityId) {
        this.commodityId = commodityId;
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

    public BigDecimal getStrikePrice() {
        return strikePrice;
    }

    public void setStrikePrice(BigDecimal strikePrice) {
        this.strikePrice = strikePrice;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getOptionType() {
        return optionType;
    }

    public void setOptionType(String optionType) {
        this.optionType = optionType;
    }
}
