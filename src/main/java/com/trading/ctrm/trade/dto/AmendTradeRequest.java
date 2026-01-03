package com.trading.ctrm.trade.dto;

import java.math.BigDecimal;

public class AmendTradeRequest {
    
    private BigDecimal price;
    private BigDecimal quantity;
    private String amendReason;
    private String amendedBy;

    // Getters and Setters
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getAmendReason() {
        return amendReason;
    }

    public void setAmendReason(String amendReason) {
        this.amendReason = amendReason;
    }

    public String getAmendedBy() {
        return amendedBy;
    }

    public void setAmendedBy(String amendedBy) {
        this.amendedBy = amendedBy;
    }
}
