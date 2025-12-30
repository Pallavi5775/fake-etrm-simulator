package com.trading.ctrm.marketdata;


import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "market_prices")
public class MarketPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String instrumentCode;   // POWER_JAN25

    @Column(nullable = false)
    private BigDecimal price;

    /* ===== Getters & Setters ===== */

    public Long getId() {
        return id;
    }

    public String getInstrumentCode() {
        return instrumentCode;
    }

    public void setInstrumentCode(String instrumentCode) {
        this.instrumentCode = instrumentCode;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}

