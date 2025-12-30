package com.trading.ctrm.instrument;

import jakarta.persistence.*;

@Entity
@Table(name = "instruments")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Instrument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String instrumentCode;   // POWER_JAN25

    private String commodity;         // POWER, GAS
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

    public String getCommodity() {
        return commodity;
    }

    public void setCommodity(String commodity) {
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
