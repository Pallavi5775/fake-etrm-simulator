package com.trading.ctrm.instrument;

public class CommoditySwapRequest {
        public String getCurrency() {
            return currency;
        }
    
    private String instrumentCode;
    private Long commodityId;
    private String currency;
    private String unit;

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
}
