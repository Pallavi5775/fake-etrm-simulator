package com.trading.ctrm.risk;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Risk Limit - Endur-style risk limit management
 * Position limits, VaR limits, concentration limits
 */
@Entity
@Table(name = "risk_limit")
public class RiskLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "limit_id")
    private Long limitId;

    @Column(name = "limit_name", nullable = false, length = 100)
    private String limitName;

    @Column(name = "limit_type", nullable = false, length = 30)
    private String limitType; // POSITION, VAR, DELTA, CONCENTRATION

    @Column(name = "limit_scope", nullable = false, length = 30)
    private String limitScope; // PORTFOLIO, COMMODITY, COUNTERPARTY

    @Column(name = "scope_value", length = 100)
    private String scopeValue; // Specific portfolio/commodity/counterparty

    // Limit values
    @Column(name = "limit_value", precision = 20, scale = 6)
    private BigDecimal limitValue;

    @Column(name = "warning_threshold", precision = 20, scale = 6)
    private BigDecimal warningThreshold;

    @Column(name = "limit_unit", length = 20)
    private String limitUnit; // MWh, USD, PERCENT

    // Status
    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "breach_action", length = 30)
    private String breachAction; // ALERT, BLOCK, ESCALATE

    // Metadata
    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "last_modified_by", length = 50)
    private String lastModifiedBy;

    @Column(name = "last_modified_at")
    private LocalDateTime lastModifiedAt;

    // Constructors
    public RiskLimit() {
        this.createdAt = LocalDateTime.now();
        this.lastModifiedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getLimitId() { return limitId; }
    public void setLimitId(Long limitId) { this.limitId = limitId; }

    public String getLimitName() { return limitName; }
    public void setLimitName(String limitName) { this.limitName = limitName; }

    public String getLimitType() { return limitType; }
    public void setLimitType(String limitType) { this.limitType = limitType; }

    public String getLimitScope() { return limitScope; }
    public void setLimitScope(String limitScope) { this.limitScope = limitScope; }

    public String getScopeValue() { return scopeValue; }
    public void setScopeValue(String scopeValue) { this.scopeValue = scopeValue; }

    public BigDecimal getLimitValue() { return limitValue; }
    public void setLimitValue(BigDecimal limitValue) { this.limitValue = limitValue; }

    public BigDecimal getWarningThreshold() { return warningThreshold; }
    public void setWarningThreshold(BigDecimal warningThreshold) { this.warningThreshold = warningThreshold; }

    public String getLimitUnit() { return limitUnit; }
    public void setLimitUnit(String limitUnit) { this.limitUnit = limitUnit; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public String getBreachAction() { return breachAction; }
    public void setBreachAction(String breachAction) { this.breachAction = breachAction; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }

    public LocalDateTime getLastModifiedAt() { return lastModifiedAt; }
    public void setLastModifiedAt(LocalDateTime lastModifiedAt) { this.lastModifiedAt = lastModifiedAt; }
}
