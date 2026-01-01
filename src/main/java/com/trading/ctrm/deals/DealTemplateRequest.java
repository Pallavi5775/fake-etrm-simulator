package com.trading.ctrm.deals;

import java.math.BigDecimal;

public class DealTemplateRequest {
    
    private String templateName;
    private Long instrumentId;
    private BigDecimal defaultQuantity;
    private BigDecimal defaultPrice;
    private boolean autoApprovalAllowed;

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
}
