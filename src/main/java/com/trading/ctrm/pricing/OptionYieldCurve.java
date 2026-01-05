package com.trading.ctrm.pricing;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "option_yield_curves", schema = "ctrm")
public class OptionYieldCurve {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "curve_name", nullable = false)
    private String curveName;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private double yield;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCurveName() { return curveName; }
    public void setCurveName(String curveName) { this.curveName = curveName; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public double getYield() { return yield; }
    public void setYield(double yield) { this.yield = yield; }
}