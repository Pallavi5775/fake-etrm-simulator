package com.trading.ctrm.pricing;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Valuation Run - batch processing metadata (Endur-style)
 */
@Entity
@Table(name = "valuation_run")
public class ValuationRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "run_id")
    private Long runId;

    @Column(name = "run_name", length = 100)
    private String runName;

    @Column(name = "valuation_date", nullable = false)
    private LocalDate valuationDate;

    @Column(name = "portfolio_filter", length = 100)
    private String portfolioFilter;

    @Column(name = "total_trades")
    private Integer totalTrades;

    @Column(name = "successful_count")
    private Integer successfulCount;

    @Column(name = "failed_count")
    private Integer failedCount;

    @Column(nullable = false, length = 20)
    private String status; // RUNNING, COMPLETED, FAILED

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "started_by", length = 50)
    private String startedBy;

    // Constructors
    public ValuationRun() {
        this.startedAt = LocalDateTime.now();
        this.status = "RUNNING";
        this.successfulCount = 0;
        this.failedCount = 0;
    }

    public ValuationRun(String runName, LocalDate valuationDate, String portfolioFilter, String startedBy) {
        this();
        this.runName = runName;
        this.valuationDate = valuationDate;
        this.portfolioFilter = portfolioFilter;
        this.startedBy = startedBy;
    }

    // Getters and Setters
    public Long getRunId() { return runId; }
    public void setRunId(Long runId) { this.runId = runId; }

    public String getRunName() { return runName; }
    public void setRunName(String runName) { this.runName = runName; }

    public LocalDate getValuationDate() { return valuationDate; }
    public void setValuationDate(LocalDate valuationDate) { this.valuationDate = valuationDate; }

    public String getPortfolioFilter() { return portfolioFilter; }
    public void setPortfolioFilter(String portfolioFilter) { this.portfolioFilter = portfolioFilter; }

    public Integer getTotalTrades() { return totalTrades; }
    public void setTotalTrades(Integer totalTrades) { this.totalTrades = totalTrades; }

    public Integer getSuccessfulCount() { return successfulCount; }
    public void setSuccessfulCount(Integer successfulCount) { this.successfulCount = successfulCount; }

    public Integer getFailedCount() { return failedCount; }
    public void setFailedCount(Integer failedCount) { this.failedCount = failedCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public String getStartedBy() { return startedBy; }
    public void setStartedBy(String startedBy) { this.startedBy = startedBy; }
}
