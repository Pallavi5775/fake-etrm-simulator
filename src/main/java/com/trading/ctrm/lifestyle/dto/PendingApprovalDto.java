package com.trading.ctrm.lifestyle.dto;



import java.time.LocalDateTime;

public class PendingApprovalDto {

    private Long tradeId;               // DB id
    private String businessTradeId;     // TRD-1002
    private String desk;                // POWER
    private String status;              // PENDING_APPROVAL
    private String requiredRole;        // RISK / OPS / COMPLIANCE
    private String triggeringEvent;     // PRICED
    private LocalDateTime requestedAt;  // when approval was requested

    // -------- Getters & Setters --------

    public Long getTradeId() {
        return tradeId;
    }

    public void setTradeId(Long tradeId) {
        this.tradeId = tradeId;
    }

    public String getBusinessTradeId() {
        return businessTradeId;
    }

    public void setBusinessTradeId(String businessTradeId) {
        this.businessTradeId = businessTradeId;
    }

    public String getDesk() {
        return desk;
    }

    public void setDesk(String desk) {
        this.desk = desk;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequiredRole() {
        return requiredRole;
    }

    public void setRequiredRole(String requiredRole) {
        this.requiredRole = requiredRole;
    }

    public String getTriggeringEvent() {
        return triggeringEvent;
    }

    public void setTriggeringEvent(String triggeringEvent) {
        this.triggeringEvent = triggeringEvent;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public String getEvent() {
        return triggeringEvent;
    }

    public void setEvent(String event) {
        this.triggeringEvent = event;
    }
}
