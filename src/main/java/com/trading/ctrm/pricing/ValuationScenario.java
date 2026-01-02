package com.trading.ctrm.pricing;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Valuation Scenario - for what-if analysis and stress testing
 */
@Entity
@Table(name = "valuation_scenario")
public class ValuationScenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scenario_id")
    private Long scenarioId;

    @Column(name = "scenario_name", nullable = false, length = 100)
    private String scenarioName;

    @Column(length = 500)
    private String description;

    @Column(name = "scenario_type", nullable = false, length = 30)
    private String scenarioType; // SPOT_SHOCK, CURVE_SHIFT, VOL_SHOCK, HISTORICAL

    @Column(name = "base_date", nullable = false)
    private LocalDate baseDate;

    // Scenario parameters stored as JSONB
    @Column(columnDefinition = "TEXT")
    private String parameters;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Constructors
    public ValuationScenario() {
        this.createdAt = LocalDateTime.now();
    }

    public ValuationScenario(String scenarioName, String scenarioType, LocalDate baseDate) {
        this();
        this.scenarioName = scenarioName;
        this.scenarioType = scenarioType;
        this.baseDate = baseDate;
    }

    // Getters and Setters
    public Long getScenarioId() { return scenarioId; }
    public void setScenarioId(Long scenarioId) { this.scenarioId = scenarioId; }

    public String getScenarioName() { return scenarioName; }
    public void setScenarioName(String scenarioName) { this.scenarioName = scenarioName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getScenarioType() { return scenarioType; }
    public void setScenarioType(String scenarioType) { this.scenarioType = scenarioType; }

    public LocalDate getBaseDate() { return baseDate; }
    public void setBaseDate(LocalDate baseDate) { this.baseDate = baseDate; }

    public String getParameters() { return parameters; }
    public void setParameters(String parameters) { this.parameters = parameters; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
