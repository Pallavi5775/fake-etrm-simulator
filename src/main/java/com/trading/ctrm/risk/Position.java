package com.trading.ctrm.risk;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Position - Endur-style position aggregation
 * Aggregates trades by portfolio, commodity, delivery period
 */
@Entity
@Table(name = "position")
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "position_id")
    private Long positionId;

    @Column(name = "position_date", nullable = false)
    private LocalDate positionDate;

    @Column(nullable = false, length = 50)
    private String portfolio;

    @Column(nullable = false, length = 50)
    private String commodity;

    @Column(name = "delivery_start")
    private LocalDate deliveryStart;

    @Column(name = "delivery_end")
    private LocalDate deliveryEnd;

    // Aggregated quantities
    @Column(name = "long_quantity", precision = 20, scale = 6)
    private BigDecimal longQuantity = BigDecimal.ZERO;

    @Column(name = "short_quantity", precision = 20, scale = 6)
    private BigDecimal shortQuantity = BigDecimal.ZERO;

    @Column(name = "net_quantity", precision = 20, scale = 6)
    private BigDecimal netQuantity = BigDecimal.ZERO;

    // Aggregated values
    @Column(name = "long_mtm", precision = 20, scale = 6)
    private BigDecimal longMtm = BigDecimal.ZERO;

    @Column(name = "short_mtm", precision = 20, scale = 6)
    private BigDecimal shortMtm = BigDecimal.ZERO;

    @Column(name = "net_mtm", precision = 20, scale = 6)
    private BigDecimal netMtm = BigDecimal.ZERO;

    // Risk metrics
    @Column(precision = 20, scale = 6)
    private BigDecimal delta = BigDecimal.ZERO;

    @Column(precision = 20, scale = 6)
    private BigDecimal gamma = BigDecimal.ZERO;

    @Column(precision = 20, scale = 6)
    private BigDecimal vega = BigDecimal.ZERO;

    // Trade count
    @Column(name = "trade_count")
    private Integer tradeCount = 0;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    // Constructors
    public Position() {
        this.lastUpdated = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getPositionId() { return positionId; }
    public void setPositionId(Long positionId) { this.positionId = positionId; }

    public LocalDate getPositionDate() { return positionDate; }
    public void setPositionDate(LocalDate positionDate) { this.positionDate = positionDate; }

    public String getPortfolio() { return portfolio; }
    public void setPortfolio(String portfolio) { this.portfolio = portfolio; }

    public String getCommodity() { return commodity; }
    public void setCommodity(String commodity) { this.commodity = commodity; }

    public LocalDate getDeliveryStart() { return deliveryStart; }
    public void setDeliveryStart(LocalDate deliveryStart) { this.deliveryStart = deliveryStart; }

    public LocalDate getDeliveryEnd() { return deliveryEnd; }
    public void setDeliveryEnd(LocalDate deliveryEnd) { this.deliveryEnd = deliveryEnd; }

    public BigDecimal getLongQuantity() { return longQuantity; }
    public void setLongQuantity(BigDecimal longQuantity) { this.longQuantity = longQuantity; }

    public BigDecimal getShortQuantity() { return shortQuantity; }
    public void setShortQuantity(BigDecimal shortQuantity) { this.shortQuantity = shortQuantity; }

    public BigDecimal getNetQuantity() { return netQuantity; }
    public void setNetQuantity(BigDecimal netQuantity) { this.netQuantity = netQuantity; }

    public BigDecimal getLongMtm() { return longMtm; }
    public void setLongMtm(BigDecimal longMtm) { this.longMtm = longMtm; }

    public BigDecimal getShortMtm() { return shortMtm; }
    public void setShortMtm(BigDecimal shortMtm) { this.shortMtm = shortMtm; }

    public BigDecimal getNetMtm() { return netMtm; }
    public void setNetMtm(BigDecimal netMtm) { this.netMtm = netMtm; }

    public BigDecimal getDelta() { return delta; }
    public void setDelta(BigDecimal delta) { this.delta = delta; }

    public BigDecimal getGamma() { return gamma; }
    public void setGamma(BigDecimal gamma) { this.gamma = gamma; }

    public BigDecimal getVega() { return vega; }
    public void setVega(BigDecimal vega) { this.vega = vega; }

    public Integer getTradeCount() { return tradeCount; }
    public void setTradeCount(Integer tradeCount) { this.tradeCount = tradeCount; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}
