package com.trading.ctrm.trade;
import com.trading.ctrm.instrument.Instrument;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.trading.ctrm.trade.EnumType.BuySell;
import jakarta.persistence.EnumType;



@Entity
@Table(name = "trades")
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ✅ REQUIRED BY JPA
    protected Trade() {
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
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

    public BigDecimal getMtm() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getMtm'");
    }

    public void setMtm(BigDecimal mtm) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setMtm'");
    }

    
}
