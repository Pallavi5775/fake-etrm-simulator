package com.trading.ctrm.trade.dto;

import com.trading.ctrm.trade.EnumType.BuySell;
import com.trading.ctrm.trade.TradeStatus;

import java.time.LocalDateTime;

public class TradeResponseDto {

    private String tradeId;
    private long amendCount;


    /** Instrument business key (NOT DB id) */
    private String instrumentSymbol;

    private String portfolio;

    private String counterparty;

    private double quantity;

    private double price;

    private BuySell buySell;

    private TradeStatus status;

    /** Optional but useful for UI */
    private LocalDateTime createdAt;

    // ------------------------
    // Constructors
    // ------------------------

    public TradeResponseDto() {
    }

    public TradeResponseDto(
            String tradeId,
            String instrumentSymbol,
            String portfolio,
            String counterparty,
            double quantity,
            double price,
            BuySell buySell,
            TradeStatus status,
            LocalDateTime createdAt
    ) {
        this.tradeId = tradeId;
        this.instrumentSymbol = instrumentSymbol;
        this.portfolio = portfolio;
        this.counterparty = counterparty;
        this.quantity = quantity;
        this.price = price;
        this.buySell = buySell;
        this.status = status;
        this.createdAt = createdAt;
    }

    // ------------------------
    // Getters & Setters
    // ------------------------

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getInstrumentSymbol() {
        return instrumentSymbol;
    }

    public void setInstrumentSymbol(String instrumentSymbol) {
        this.instrumentSymbol = instrumentSymbol;
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

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public BuySell getBuySell() {
        return buySell;
    }

    public void setBuySell(BuySell buySell) {
        this.buySell = buySell;
    }

    public TradeStatus getStatus() {
        return status;
    }

    public void setStatus(TradeStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public long getAmendCount() {
        return amendCount;
    }
    public void setAmendCount(long amendCount) {
        this.amendCount = amendCount;
    }
}
