package com.trading.ctrm.pricing;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Market Curve entity - stores forward curves for commodities, interest rates, FX
 * Endur-style market data infrastructure
 */
@Entity
@Table(name = "market_curve")
public class MarketCurve {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "curve_id")
    private Long curveId;

    @Column(name = "curve_name", nullable = false, length = 100)
    private String curveName;

    @Column(length = 50)
    private String commodity;

    @Column(name = "curve_type", nullable = false, length = 30)
    private String curveType; // FORWARD, DISCOUNT, FX

    @Column(length = 3)
    private String currency;

    @Column(name = "pricing_date", nullable = false)
    private LocalDate pricingDate;

    @Column(length = 50)
    private String source; // BLOOMBERG, REUTERS, MANUAL

    @OneToMany(mappedBy = "curve", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MarketCurvePoint> points = new ArrayList<>();

    // Constructors
    public MarketCurve() {}

    public MarketCurve(String curveName, String curveType, LocalDate pricingDate) {
        this.curveName = curveName;
        this.curveType = curveType;
        this.pricingDate = pricingDate;
    }

    // Getters and Setters
    public Long getCurveId() { return curveId; }
    public void setCurveId(Long curveId) { this.curveId = curveId; }

    public String getCurveName() { return curveName; }
    public void setCurveName(String curveName) { this.curveName = curveName; }

    public String getCommodity() { return commodity; }
    public void setCommodity(String commodity) { this.commodity = commodity; }

    public String getCurveType() { return curveType; }
    public void setCurveType(String curveType) { this.curveType = curveType; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public LocalDate getPricingDate() { return pricingDate; }
    public void setPricingDate(LocalDate pricingDate) { this.pricingDate = pricingDate; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public List<MarketCurvePoint> getPoints() { return points; }
    public void setPoints(List<MarketCurvePoint> points) { this.points = points; }

    public void addPoint(MarketCurvePoint point) {
        points.add(point);
        point.setCurve(this);
    }
}
