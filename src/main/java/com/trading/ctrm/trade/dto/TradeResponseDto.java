package com.trading.ctrm.trade.dto;

import com.trading.ctrm.trade.EnumType.BuySell;
import com.trading.ctrm.trade.TradeStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TradeResponseDto {

    private String tradeId;
    private long amendCount;


    /** Instrument business key (NOT DB id) */
    private String instrumentSymbol;

    private String portfolio;

    private String counterparty;

    private BigDecimal quantity;

    private BigDecimal price;

    private BuySell buySell;

    private TradeStatus status;

    /** Optional but useful for UI */
    private LocalDateTime createdAt;
    
    /** Approval workflow fields */
    private String pendingApprovalRole;
    private Integer currentApprovalLevel;
    private Long matchedRuleId;
    
    /** Created by user */
    private String createdBy;
    
    /** Valuation context information */
    private BigDecimal mtm;
    private BigDecimal delta;
    private BigDecimal gamma;
    private BigDecimal vega;
    private String commodity;
    private String instrumentType;

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
            BigDecimal quantity,
            BigDecimal price,
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

    public String getPendingApprovalRole() {
        return pendingApprovalRole;
    }

    public void setPendingApprovalRole(String pendingApprovalRole) {
        this.pendingApprovalRole = pendingApprovalRole;
    }

    public Integer getCurrentApprovalLevel() {
        return currentApprovalLevel;
    }

    public void setCurrentApprovalLevel(Integer currentApprovalLevel) {
        this.currentApprovalLevel = currentApprovalLevel;
    }

    public Long getMatchedRuleId() {
        return matchedRuleId;
    }

    public void setMatchedRuleId(Long matchedRuleId) {
        this.matchedRuleId = matchedRuleId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public BigDecimal getMtm() {
        return mtm;
    }

    public void setMtm(BigDecimal mtm) {
        this.mtm = mtm;
    }

    public BigDecimal getDelta() {
        return delta;
    }

    public void setDelta(BigDecimal delta) {
        this.delta = delta;
    }

    public BigDecimal getGamma() {
        return gamma;
    }

    public void setGamma(BigDecimal gamma) {
        this.gamma = gamma;
    }

    public BigDecimal getVega() {
        return vega;
    }

    public void setVega(BigDecimal vega) {
        this.vega = vega;
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
}
