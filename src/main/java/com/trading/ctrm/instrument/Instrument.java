package com.trading.ctrm.instrument;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.Hibernate;

@Entity
@Table(name = "instruments")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Instrument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String instrumentCode;   // POWER_JAN25
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commodity_id")
    @JsonIgnore
    private Commodity commodity;
    
    private String currency;          // EUR
    private String unit;              // MWh

    @Enumerated(EnumType.STRING)
    private InstrumentType instrumentType;

    /* ===== Getters / Setters ===== */

    public Long getId() {
        return id;
    }

    public String getInstrumentCode() {
        return instrumentCode;
    }

    public void setInstrumentCode(String instrumentCode) {
        this.instrumentCode = instrumentCode;
    }

    @JsonProperty("commodity_name")
    public String getCommodity() {
        if (commodity == null) {
            return null;
        }
        // Check if the commodity is initialized (not a lazy proxy)
        if (Hibernate.isInitialized(commodity)) {
            return commodity.getName();
        } else {
            // If not initialized, try to access it safely
            try {
                return commodity.getName();
            } catch (Exception e) {
                // If lazy loading fails, return null
                return null;
            }
        }
    }

    /**
     * Deprecated: use setCommodity(Commodity commodity) instead
     */
    public void setCommodity(String commodity) {
        throw new UnsupportedOperationException("Use setCommodity(Commodity commodity) instead");
    }

    @JsonIgnore
    public Commodity getCommodityEntity() {
        return commodity;
    }

    public void setCommodity(Commodity commodity) {
        this.commodity = commodity;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public InstrumentType getInstrumentType() {
        return instrumentType;
    }

    protected void setInstrumentType(InstrumentType instrumentType) {
        this.instrumentType = instrumentType;
    }
}
