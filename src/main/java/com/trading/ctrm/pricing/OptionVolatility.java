package com.trading.ctrm.pricing;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "option_volatility", schema = "ctrm")
public class OptionVolatility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String instrumentCode;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private double value;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInstrumentCode() { return instrumentCode; }
    public void setInstrumentCode(String instrumentCode) { this.instrumentCode = instrumentCode; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
}