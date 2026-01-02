package com.trading.ctrm.pricing;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Volatility Point - individual point on the vol surface (strike x expiry)
 */
@Entity
@Table(name = "volatility_point")
public class VolatilityPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_id")
    private Long pointId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "surface_id", nullable = false)
    private VolatilitySurface surface;

    @Column(precision = 20, scale = 6)
    private BigDecimal strike;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "implied_vol", nullable = false, precision = 10, scale = 6)
    private BigDecimal impliedVol; // In decimal (e.g., 0.25 = 25%)

    // Constructors
    public VolatilityPoint() {}

    public VolatilityPoint(BigDecimal strike, LocalDate expiryDate, BigDecimal impliedVol) {
        this.strike = strike;
        this.expiryDate = expiryDate;
        this.impliedVol = impliedVol;
    }

    // Getters and Setters
    public Long getPointId() { return pointId; }
    public void setPointId(Long pointId) { this.pointId = pointId; }

    public VolatilitySurface getSurface() { return surface; }
    public void setSurface(VolatilitySurface surface) { this.surface = surface; }

    public BigDecimal getStrike() { return strike; }
    public void setStrike(BigDecimal strike) { this.strike = strike; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public BigDecimal getImpliedVol() { return impliedVol; }
    public void setImpliedVol(BigDecimal impliedVol) { this.impliedVol = impliedVol; }
}
