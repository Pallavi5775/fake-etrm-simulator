package com.trading.ctrm.risk;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Risk Limit Breach - tracks limit violations
 */
@Entity
@Table(name = "risk_limit_breach")
public class RiskLimitBreach {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "breach_id")
    private Long breachId;

    @Column(name = "limit_id", nullable = false)
    private Long limitId;

    @Column(name = "breach_date", nullable = false)
    private LocalDateTime breachDate;

    @Column(name = "current_value", precision = 20, scale = 6)
    private BigDecimal currentValue;

    @Column(name = "limit_value", precision = 20, scale = 6)
    private BigDecimal limitValue;

    @Column(name = "breach_amount", precision = 20, scale = 6)
    private BigDecimal breachAmount;

    @Column(name = "breach_percent", precision = 10, scale = 6)
    private BigDecimal breachPercent;

    @Column(name = "severity", length = 20)
    private String severity; // WARNING, BREACH, CRITICAL

    @Column(name = "breach_status", length = 20)
    private String breachStatus; // ACTIVE, RESOLVED, ACKNOWLEDGED

    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;

    @Column(name = "resolved_by", length = 50)
    private String resolvedBy;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    // Constructors
    public RiskLimitBreach() {
        this.breachDate = LocalDateTime.now();
        this.breachStatus = "ACTIVE";
    }

    // Getters and Setters
    public Long getBreachId() { return breachId; }
    public void setBreachId(Long breachId) { this.breachId = breachId; }

    public Long getLimitId() { return limitId; }
    public void setLimitId(Long limitId) { this.limitId = limitId; }

    public LocalDateTime getBreachDate() { return breachDate; }
    public void setBreachDate(LocalDateTime breachDate) { this.breachDate = breachDate; }

    public BigDecimal getCurrentValue() { return currentValue; }
    public void setCurrentValue(BigDecimal currentValue) { this.currentValue = currentValue; }

    public BigDecimal getLimitValue() { return limitValue; }
    public void setLimitValue(BigDecimal limitValue) { this.limitValue = limitValue; }

    public BigDecimal getBreachAmount() { return breachAmount; }
    public void setBreachAmount(BigDecimal breachAmount) { this.breachAmount = breachAmount; }

    public BigDecimal getBreachPercent() { return breachPercent; }
    public void setBreachPercent(BigDecimal breachPercent) { this.breachPercent = breachPercent; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getBreachStatus() { return breachStatus; }
    public void setBreachStatus(String breachStatus) { this.breachStatus = breachStatus; }

    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }

    public String getResolvedBy() { return resolvedBy; }
    public void setResolvedBy(String resolvedBy) { this.resolvedBy = resolvedBy; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
}
