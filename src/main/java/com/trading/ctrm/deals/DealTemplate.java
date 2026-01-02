package com.trading.ctrm.deals;

import com.trading.ctrm.instrument.Instrument;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "deal_templates")
public class DealTemplate {

    /* =========================
       Primary Key
       ========================= */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* =========================
       Template Metadata
       ========================= */

    @Column(nullable = false, unique = true)
    private String templateName;          // Renewable Power Forward

    private String commodity;             // POWER, GAS
    private String instrumentType;         // FORWARD, PPA, OPTION

    /* =========================
       Instrument Association
       ========================= */

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "instrument_id", nullable = false)
    private Instrument instrument;

    /* =========================
       Pricing & Workflow
       ========================= */

    private String pricingModel;           // POWER_FORWARD, RENEWABLE_FORECAST
    private boolean autoApprovalAllowed;

    /* =========================
       Default Trade Values
       ========================= */

    private BigDecimal defaultQuantity;
    private BigDecimal defaultPrice;

    private String unit;                   // MWh
    private String currency;               // EUR

    /* =========================
       Risk / Approval
       ========================= */

    private BigDecimal mtmApprovalThreshold;

    /* =========================
       Getters and Setters
       ========================= */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
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

    public Instrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

    public String getPricingModel() {
        return pricingModel;
    }

    public void setPricingModel(String pricingModel) {
        this.pricingModel = pricingModel;
    }

    public boolean isAutoApprovalAllowed() {
        return autoApprovalAllowed;
    }

    public void setAutoApprovalAllowed(boolean autoApprovalAllowed) {
        this.autoApprovalAllowed = autoApprovalAllowed;
    }

    public BigDecimal getDefaultQuantity() {
        return defaultQuantity;
    }

    public void setDefaultQuantity(BigDecimal defaultQuantity) {
        this.defaultQuantity = defaultQuantity;
    }

    public BigDecimal getDefaultPrice() {
        return defaultPrice;
    }

    public void setDefaultPrice(BigDecimal defaultPrice) {
        this.defaultPrice = defaultPrice;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getMtmApprovalThreshold() {
        return mtmApprovalThreshold;
    }

    public void setMtmApprovalThreshold(BigDecimal mtmApprovalThreshold) {
        this.mtmApprovalThreshold = mtmApprovalThreshold;
    }
}
