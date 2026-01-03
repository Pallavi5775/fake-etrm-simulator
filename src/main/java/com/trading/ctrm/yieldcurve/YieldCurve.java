package com.trading.ctrm.yieldcurve;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "yield_curve")
public class YieldCurve {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String curveName;
    private LocalDate date;
    private Double yield;
    // Add more fields as needed

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCurveName() { return curveName; }
    public void setCurveName(String curveName) { this.curveName = curveName; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public Double getYield() { return yield; }
    public void setYield(Double yield) { this.yield = yield; }
}
