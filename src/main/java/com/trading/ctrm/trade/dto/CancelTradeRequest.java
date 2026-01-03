package com.trading.ctrm.trade.dto;

public class CancelTradeRequest {
    
    private String cancelReason;
    private String cancelledBy;

    // Getters and Setters
    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getCancelledBy() {
        return cancelledBy;
    }

    public void setCancelledBy(String cancelledBy) {
        this.cancelledBy = cancelledBy;
    }
}
