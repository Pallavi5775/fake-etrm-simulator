package com.trading.ctrm.trade.dto;

import com.trading.ctrm.trade.StrategyType;
import java.time.LocalDate;
import java.util.List;

public class MultiLegTradeRequest {
    
    private StrategyType strategyType;
    private String portfolio;
    private String counterparty;
    private String createdByUser;
    private LocalDate tradeDate;
    private List<TradeLegRequest> legs;
    private String notes;

    // Getters and Setters
    public StrategyType getStrategyType() {
        return strategyType;
    }

    public void setStrategyType(StrategyType strategyType) {
        this.strategyType = strategyType;
    }

    public String getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(String portfolio) {
        this.portfolio = portfolio;
    }

    public String getCounterparty() {
        return counterparty;
    }

    public void setCounterparty(String counterparty) {
        this.counterparty = counterparty;
    }

    public String getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(String createdByUser) {
        this.createdByUser = createdByUser;
    }

    public LocalDate getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(LocalDate tradeDate) {
        this.tradeDate = tradeDate;
    }

    public List<TradeLegRequest> getLegs() {
        return legs;
    }

    public void setLegs(List<TradeLegRequest> legs) {
        this.legs = legs;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
