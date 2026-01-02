package com.trading.ctrm.pricing;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Scenario Run Result - results of scenario analysis
 */
@Entity
@Table(name = "scenario_result")
public class ScenarioResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Long resultId;

    @Column(name = "scenario_id", nullable = false)
    private Long scenarioId;

    @Column(name = "trade_id", nullable = false)
    private Long tradeId;

    // Base case
    @Column(name = "base_mtm", precision = 20, scale = 6)
    private BigDecimal baseMtm;

    // Scenario case
    @Column(name = "scenario_mtm", precision = 20, scale = 6)
    private BigDecimal scenarioMtm;

    // Impact
    @Column(name = "pnl_impact", precision = 20, scale = 6)
    private BigDecimal pnlImpact;

    @Column(name = "pnl_impact_pct", precision = 10, scale = 6)
    private BigDecimal pnlImpactPct;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Constructors
    public ScenarioResult() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getResultId() { return resultId; }
    public void setResultId(Long resultId) { this.resultId = resultId; }

    public Long getScenarioId() { return scenarioId; }
    public void setScenarioId(Long scenarioId) { this.scenarioId = scenarioId; }

    public Long getTradeId() { return tradeId; }
    public void setTradeId(Long tradeId) { this.tradeId = tradeId; }

    public BigDecimal getBaseMtm() { return baseMtm; }
    public void setBaseMtm(BigDecimal baseMtm) { this.baseMtm = baseMtm; }

    public BigDecimal getScenarioMtm() { return scenarioMtm; }
    public void setScenarioMtm(BigDecimal scenarioMtm) { this.scenarioMtm = scenarioMtm; }

    public BigDecimal getPnlImpact() { return pnlImpact; }
    public void setPnlImpact(BigDecimal pnlImpact) { this.pnlImpact = pnlImpact; }

    public BigDecimal getPnlImpactPct() { return pnlImpactPct; }
    public void setPnlImpactPct(BigDecimal pnlImpactPct) { this.pnlImpactPct = pnlImpactPct; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
