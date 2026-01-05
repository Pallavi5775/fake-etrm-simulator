package com.trading.ctrm.trade;
import com.trading.ctrm.instrument.Instrument;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.trading.ctrm.trade.EnumType.BuySell;
import jakarta.persistence.EnumType;



@Entity
@Table(name = "trades")
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal mtm;

    @Column(name = "template_id")
    private Long templateId;

    @Column(name = "trade_id", nullable = false, unique = true)
    private String tradeId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "instrument_id", nullable = false)
    private Instrument instrument;

    @Column(nullable = false)
    private String counterparty;

    @Column(nullable = false)
    private String portfolio;

    @Column(nullable = false)
    private BigDecimal quantity;

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BuySell buySell;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TradeStatus status;

    @Column(name = "pending_approval_role")
    private String pendingApprovalRole; // RISK, OPS, COMPLIANCE

    @Column(name = "current_approval_level")
    private Integer currentApprovalLevel; // 1, 2, 3...

    @Column(name = "matched_rule_id")
    private Long matchedRuleId; // ID of the approval rule that was matched

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "trade_date")
    private LocalDate tradeDate;

    @Column(name = "is_multi_leg")
    private Boolean isMultiLeg = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "strategy_type")
    private StrategyType strategyType;

    @Transient
    private java.util.List<TradeLeg> legs;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ✅ REQUIRED BY JPA
    protected Trade() {
    }

    // Factory method for creating trades programmatically
    public static Trade create() {
        try {
            Trade trade = Trade.class.getDeclaredConstructor().newInstance();
            return trade;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Trade instance", e);
        }
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ===== Getters & Setters =====

    public Long getTemplateId() {
    return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

   




    public Long getId() {
        return id;
    }

    public String getTradeId() {
        return tradeId;
    }

    public String getPendingApprovalRole() {
        return pendingApprovalRole;
    }

    public void setPendingApprovalRole(String pendingApprovalRole) {
        this.pendingApprovalRole = pendingApprovalRole;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

    public String getCounterparty() {
        return counterparty;
    }

    public void setCounterparty(String counterparty) {
        this.counterparty = counterparty;
    }

    public String getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(String portfolio) {
        this.portfolio = portfolio;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public BigDecimal getMtm() {
        return mtm;
    }

    public void setMtm(BigDecimal mtm) {
        this.mtm = mtm;
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

    public LocalDate getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(LocalDate tradeDate) {
        this.tradeDate = tradeDate;
    }

    public Boolean getIsMultiLeg() {
        return isMultiLeg;
    }

    public void setIsMultiLeg(Boolean isMultiLeg) {
        this.isMultiLeg = isMultiLeg;
    }

    public StrategyType getStrategyType() {
        return strategyType;
    }

    public void setStrategyType(StrategyType strategyType) {
        this.strategyType = strategyType;
    }

    public java.util.List<TradeLeg> getLegs() {
        return legs;
    }

    public void setLegs(java.util.List<TradeLeg> legs) {
        this.legs = legs;
    }
}
