package com.trading.ctrm.trade.dto;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

public class SettleTradeRequest {
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate settlementDate;
    private String settledBy;
    private String notes;

    // Getters and Setters
    public LocalDate getSettlementDate() {
        return settlementDate;
    }

    public void setSettlementDate(LocalDate settlementDate) {
        this.settlementDate = settlementDate;
    }

    public String getSettledBy() {
        return settledBy;
    }

    public void setSettledBy(String settledBy) {
        this.settledBy = settledBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
