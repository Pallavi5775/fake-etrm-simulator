package com.trading.ctrm.common;

import java.math.BigDecimal;

import com.trading.ctrm.instrument.Instrument;
import jakarta.persistence.*;

@Entity
@Table(name = "portfolio_positions")
public class PortfolioPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String portfolio;

    // ✅ CORRECT: relationship to Instrument master
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "instrument_id", nullable = false)
    private Instrument instrument;

    @Column(name = "net_quantity", nullable = false)
    private BigDecimal netQuantity;


    public String getInstrumentCode() {
    return instrument.getInstrumentCode();
}

    // ✅ Required by JPA
    protected PortfolioPosition() {
    }

    public PortfolioPosition(String portfolio, Instrument instrument, BigDecimal netQuantity) {
        this.portfolio = portfolio;
        this.instrument = instrument;
        this.netQuantity = netQuantity;
    }

    // ===== Getters & Setters =====

    public Long getId() {
        return id;
    }

    public String getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(String portfolio) {
        this.portfolio = portfolio;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

    public BigDecimal getNetQuantity() {
        return netQuantity;
    }

    public void setNetQuantity(BigDecimal netQuantity) {
        this.netQuantity = netQuantity;
    }
}
