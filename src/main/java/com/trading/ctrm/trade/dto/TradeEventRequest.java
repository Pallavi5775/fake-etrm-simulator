package com.trading.ctrm.trade.dto;

import com.trading.ctrm.trade.TradeEventType;

import java.math.BigDecimal;

import com.trading.ctrm.trade.EnumType.BuySell;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class TradeEventRequest {

    private TradeEventType eventType; 

    @NotBlank
    private String tradeId;

    @NotBlank
    private String instrumentSymbol;   // ✅ symbol only

    @NotBlank
    private String portfolio;

    @NotBlank
    private String counterparty;

    @Positive
    private BigDecimal quantity;

    @Positive
    private BigDecimal price;

    @NotNull
    private BuySell buySell;

    /* =====================
       Getters & Setters
       ===================== */

    public TradeEventType getEventType() {      // ✅ REQUIRED
        return eventType;
    }

    public void setEventType(TradeEventType eventType) {   // ✅ REQUIRED
        this.eventType = eventType;
    }
   

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

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BuySell getBuySell() {
        return buySell;
    }

    public void setBuySell(BuySell buySell) {
        this.buySell = buySell;
    }
}
