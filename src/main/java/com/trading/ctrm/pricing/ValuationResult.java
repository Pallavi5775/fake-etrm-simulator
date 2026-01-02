package com.trading.ctrm.pricing;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Valuation Result - comprehensive pricing output (Endur-style)
 * Stores MTM, Greeks, risk metrics, and context
 */
@Entity
@Table(name = "valuation_result")
public class ValuationResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Long resultId;

    @Column(name = "trade_id", nullable = false)
    private Long tradeId;

    @Column(name = "valuation_run_id")
    private Long valuationRunId;

    @Column(name = "pricing_date", nullable = false)
    private java.time.LocalDate pricingDate;

    // ===== MTM Components =====
    @Column(name = "mtm_total", precision = 20, scale = 6)
    private BigDecimal mtmTotal;

    @Column(name = "mtm_spot", precision = 20, scale = 6)
    private BigDecimal mtmSpot;

    @Column(name = "mtm_forward", precision = 20, scale = 6)
    private BigDecimal mtmForward;

    @Column(name = "mtm_time_value", precision = 20, scale = 6)
    private BigDecimal mtmTimeValue;

    // ===== Greeks (Options) =====
    @Column(precision = 20, scale = 6)
    private BigDecimal delta;

    @Column(precision = 20, scale = 6)
    private BigDecimal gamma;

    @Column(precision = 20, scale = 6)
    private BigDecimal vega;

    @Column(precision = 20, scale = 6)
    private BigDecimal theta;

    @Column(precision = 20, scale = 6)
    private BigDecimal rho;

    // ===== Risk Metrics =====
    @Column(name = "var_1day", precision = 20, scale = 6)
    private BigDecimal var1Day;

    @Column(name = "stress_test_result", precision = 20, scale = 6)
    private BigDecimal stressTestResult;

    // ===== Context (stored as JSON) =====
    @Column(name = "market_context", columnDefinition = "jsonb")
    private String marketContext;

    @Column(name = "pricing_context", columnDefinition = "jsonb")
    private String pricingContext;

    @Column(name = "risk_context", columnDefinition = "jsonb")
    private String riskContext;

    // ===== Metadata =====
    @Column(name = "pricing_model", length = 50)
    private String pricingModel;

    @Column(name = "calc_timestamp")
    private LocalDateTime calcTimestamp;

    @Column(name = "calc_duration_ms")
    private Integer calcDurationMs;

    @Column(name = "warnings", columnDefinition = "text[]")
    private String[] warnings;

    // Transient fields for in-memory operations
    @Transient
    private Map<String, BigDecimal> greeks = new HashMap<>();

    // Constructors
    public ValuationResult() {
        this.calcTimestamp = LocalDateTime.now();
        this.pricingDate = java.time.LocalDate.now();
    }

    // Builder pattern for easy construction
    public static class Builder {
        private ValuationResult result = new ValuationResult();

        public Builder tradeId(Long tradeId) {
            result.tradeId = tradeId;
            return this;
        }

        public Builder mtmTotal(BigDecimal mtm) {
            result.mtmTotal = mtm;
            return this;
        }

        public Builder mtmComponents(BigDecimal spot, BigDecimal forward, BigDecimal timeValue) {
            result.mtmSpot = spot;
            result.mtmForward = forward;
            result.mtmTimeValue = timeValue;
            return this;
        }

        public Builder greeks(Map<String, BigDecimal> greeks) {
            result.delta = greeks.get("delta");
            result.gamma = greeks.get("gamma");
            result.vega = greeks.get("vega");
            result.theta = greeks.get("theta");
            result.rho = greeks.get("rho");
            result.greeks = greeks;
            return this;
        }

        public Builder pricingModel(String model) {
            result.pricingModel = model;
            return this;
        }

        public Builder warnings(String... warnings) {
            result.warnings = warnings;
            return this;
        }

        public ValuationResult build() {
            return result;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters and Setters
    public Long getResultId() { return resultId; }
    public void setResultId(Long resultId) { this.resultId = resultId; }

    public Long getTradeId() { return tradeId; }
    public void setTradeId(Long tradeId) { this.tradeId = tradeId; }

    public Long getValuationRunId() { return valuationRunId; }
    public void setValuationRunId(Long valuationRunId) { this.valuationRunId = valuationRunId; }

    public java.time.LocalDate getPricingDate() { return pricingDate; }
    public void setPricingDate(java.time.LocalDate pricingDate) { this.pricingDate = pricingDate; }

    public BigDecimal getMtmTotal() { return mtmTotal; }
    public void setMtmTotal(BigDecimal mtmTotal) { this.mtmTotal = mtmTotal; }

    public BigDecimal getMtmSpot() { return mtmSpot; }
    public void setMtmSpot(BigDecimal mtmSpot) { this.mtmSpot = mtmSpot; }

    public BigDecimal getMtmForward() { return mtmForward; }
    public void setMtmForward(BigDecimal mtmForward) { this.mtmForward = mtmForward; }

    public BigDecimal getMtmTimeValue() { return mtmTimeValue; }
    public void setMtmTimeValue(BigDecimal mtmTimeValue) { this.mtmTimeValue = mtmTimeValue; }

    public BigDecimal getDelta() { return delta; }
    public void setDelta(BigDecimal delta) { this.delta = delta; }

    public BigDecimal getGamma() { return gamma; }
    public void setGamma(BigDecimal gamma) { this.gamma = gamma; }

    public BigDecimal getVega() { return vega; }
    public void setVega(BigDecimal vega) { this.vega = vega; }

    public BigDecimal getTheta() { return theta; }
    public void setTheta(BigDecimal theta) { this.theta = theta; }

    public BigDecimal getRho() { return rho; }
    public void setRho(BigDecimal rho) { this.rho = rho; }

    public String getPricingModel() { return pricingModel; }
    public void setPricingModel(String pricingModel) { this.pricingModel = pricingModel; }

    public LocalDateTime getCalcTimestamp() { return calcTimestamp; }
    public void setCalcTimestamp(LocalDateTime calcTimestamp) { this.calcTimestamp = calcTimestamp; }

    public Integer getCalcDurationMs() { return calcDurationMs; }
    public void setCalcDurationMs(Integer calcDurationMs) { this.calcDurationMs = calcDurationMs; }

    public String[] getWarnings() { return warnings; }
    public void setWarnings(String[] warnings) { this.warnings = warnings; }

    public Map<String, BigDecimal> getGreeks() { return greeks; }
    public void setGreeks(Map<String, BigDecimal> greeks) { this.greeks = greeks; }

    public String getMarketContext() { return marketContext; }
    public void setMarketContext(String marketContext) { this.marketContext = marketContext; }

    public String getPricingContext() { return pricingContext; }
    public void setPricingContext(String pricingContext) { this.pricingContext = pricingContext; }

    public String getRiskContext() { return riskContext; }
    public void setRiskContext(String riskContext) { this.riskContext = riskContext; }
}
