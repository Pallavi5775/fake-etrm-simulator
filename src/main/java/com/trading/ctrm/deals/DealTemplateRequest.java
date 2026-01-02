package com.trading.ctrm.deals;

import java.math.BigDecimal;

public class DealTemplateRequest {
    
    private String templateName;
    private Long instrumentId;
    private BigDecimal defaultQuantity;
    private BigDecimal defaultPrice;
    private boolean autoApprovalAllowed;
    
    // Optional overrides - if not provided, inherit from instrument
    private String commodity;
    private String instrumentType;
    private String unit;
    private String currency;
    private BigDecimal mtmApprovalThreshold;

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public Long getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(Long instrumentId) {
        this.instrumentId = instrumentId;
    }

    public BigDecimal getDefaultQuantity() {
        return defaultQuantity;
    }

    public void setDefaultQuantity(BigDecimal defaultQuantity) {
        this.defaultQuantity = defaultQuantity;
    }

    public BigDecimal getDefaultPrice() {
        return defaultPrice;
    }

    public void setDefaultPrice(BigDecimal defaultPrice) {
        this.defaultPrice = defaultPrice;
    }

    public boolean isAutoApprovalAllowed() {
        return autoApprovalAllowed;
    }

    public void setAutoApprovalAllowed(boolean autoApprovalAllowed) {
        this.autoApprovalAllowed = autoApprovalAllowed;
    }

    public String getCommodity() {
        return commodity;
    }

    public void setCommodity(String commodity) {
        this.commodity = commodity;
    }

    public String getInstrumentType() {
        return instrumentType;
    }

    public void setInstrumentType(String instrumentType) {
        this.instrumentType = instrumentType;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getMtmApprovalThreshold() {
        return mtmApprovalThreshold;
    }

    public void setMtmApprovalThreshold(BigDecimal mtmApprovalThreshold) {
        this.mtmApprovalThreshold = mtmApprovalThreshold;
    }
}
