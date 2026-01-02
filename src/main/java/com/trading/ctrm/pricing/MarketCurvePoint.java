package com.trading.ctrm.pricing;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Market Curve Point - individual tenor points on a curve
 */
@Entity
@Table(name = "market_curve_point")
public class MarketCurvePoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_id")
    private Long pointId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curve_id", nullable = false)
    private MarketCurve curve;

    @Column(name = "tenor_date", nullable = false)
    private LocalDate tenorDate;

    @Column(name = "tenor_label", length = 20)
    private String tenorLabel; // 1M, 3M, 1Y

    @Column(name = "price_value", nullable = false, precision = 20, scale = 6)
    private BigDecimal priceValue;

    // Constructors
    public MarketCurvePoint() {}

    public MarketCurvePoint(LocalDate tenorDate, String tenorLabel, BigDecimal priceValue) {
        this.tenorDate = tenorDate;
        this.tenorLabel = tenorLabel;
        this.priceValue = priceValue;
    }

    // Getters and Setters
    public Long getPointId() { return pointId; }
    public void setPointId(Long pointId) { this.pointId = pointId; }

    public MarketCurve getCurve() { return curve; }
    public void setCurve(MarketCurve curve) { this.curve = curve; }

    public LocalDate getTenorDate() { return tenorDate; }
    public void setTenorDate(LocalDate tenorDate) { this.tenorDate = tenorDate; }

    public String getTenorLabel() { return tenorLabel; }
    public void setTenorLabel(String tenorLabel) { this.tenorLabel = tenorLabel; }

    public BigDecimal getPriceValue() { return priceValue; }
    public void setPriceValue(BigDecimal priceValue) { this.priceValue = priceValue; }
}
