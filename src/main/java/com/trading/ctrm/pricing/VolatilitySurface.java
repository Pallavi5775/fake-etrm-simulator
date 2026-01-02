package com.trading.ctrm.pricing;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Volatility Surface entity - for options pricing
 * Stores implied volatility across strike prices and expiry dates
 */
@Entity
@Table(name = "volatility_surface")
public class VolatilitySurface {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "surface_id")
    private Long surfaceId;

    @Column(name = "surface_name", nullable = false, length = 100)
    private String surfaceName;

    @Column(nullable = false, length = 50)
    private String underlying;

    @Column(name = "surface_type", nullable = false, length = 30)
    private String surfaceType; // IMPLIED, HISTORICAL

    @Column(name = "pricing_date", nullable = false)
    private LocalDate pricingDate;

    @Column(length = 3)
    private String currency;

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;

    @OneToMany(mappedBy = "surface", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VolatilityPoint> points = new ArrayList<>();

    // Constructors
    public VolatilitySurface() {
        this.createdAt = java.time.LocalDateTime.now();
    }

    public VolatilitySurface(String surfaceName, String underlying, String surfaceType, LocalDate pricingDate) {
        this();
        this.surfaceName = surfaceName;
        this.underlying = underlying;
        this.surfaceType = surfaceType;
        this.pricingDate = pricingDate;
    }

    // Helper methods
    public void addPoint(VolatilityPoint point) {
        points.add(point);
        point.setSurface(this);
    }

    // Getters and Setters
    public Long getSurfaceId() { return surfaceId; }
    public void setSurfaceId(Long surfaceId) { this.surfaceId = surfaceId; }

    public String getSurfaceName() { return surfaceName; }
    public void setSurfaceName(String surfaceName) { this.surfaceName = surfaceName; }

    public String getUnderlying() { return underlying; }
    public void setUnderlying(String underlying) { this.underlying = underlying; }

    public String getSurfaceType() { return surfaceType; }
    public void setSurfaceType(String surfaceType) { this.surfaceType = surfaceType; }

    public LocalDate getPricingDate() { return pricingDate; }
    public void setPricingDate(LocalDate pricingDate) { this.pricingDate = pricingDate; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public java.time.LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<VolatilityPoint> getPoints() { return points; }
    public void setPoints(List<VolatilityPoint> points) { this.points = points; }
}
