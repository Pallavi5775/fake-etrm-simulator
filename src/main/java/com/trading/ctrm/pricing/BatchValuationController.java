package com.trading.ctrm.pricing;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Batch Valuation Controller - Endur-style portfolio operations
 */
@RestController
@RequestMapping("/api/valuation")
public class BatchValuationController {

    private static final Logger log = LoggerFactory.getLogger(BatchValuationController.class);

    private final BatchValuationService batchValuationService;
    private final PnlAttributionService pnlAttributionService;
    private final ScenarioService scenarioService;

    public BatchValuationController(
            BatchValuationService batchValuationService,
            PnlAttributionService pnlAttributionService,
            ScenarioService scenarioService) {
        this.batchValuationService = batchValuationService;
        this.pnlAttributionService = pnlAttributionService;
        this.scenarioService = scenarioService;
    }

    /**
     * Run batch valuation
     */
    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> runBatchValuation(@RequestBody BatchValuationRequest request) {
        log.info("Running batch valuation for date: {}", request.getValuationDate());

        ValuationRun run = batchValuationService.runBatchValuation(
            request.getValuationDate(),
            request.getPortfolioFilter(),
            request.getStartedBy()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("runId", run.getRunId());
        response.put("status", run.getStatus());
        response.put("totalTrades", run.getTotalTrades());
        response.put("successfulCount", run.getSuccessfulCount());
        response.put("failedCount", run.getFailedCount());
        response.put("startedAt", run.getStartedAt());
        response.put("completedAt", run.getCompletedAt());

        return ResponseEntity.ok(response);
    }

    /**
     * Get recent valuation runs
     */
    @GetMapping("/batch/runs")
    public ResponseEntity<List<ValuationRun>> getRecentRuns() {
        return ResponseEntity.ok(batchValuationService.getRecentRuns());
    }

    /**
     * Calculate daily P&L
     */
    @PostMapping("/pnl/calculate")
    public ResponseEntity<Map<String, Object>> calculatePnl(@RequestBody PnlCalculationRequest request) {
        // Default to today if pnlDate is null
        LocalDate pnlDate = request.getPnlDate() != null ? request.getPnlDate() : LocalDate.now();
        log.info("Calculating P&L for date: {}", pnlDate);

        pnlAttributionService.calculateDailyPnl(pnlDate);

        BigDecimal totalPnl = pnlAttributionService.getTotalPnl(pnlDate);
        List<PnlExplain> pnlDetails = pnlAttributionService.getPnlForDate(pnlDate);

        Map<String, Object> response = new HashMap<>();
        response.put("pnlDate", pnlDate);
        response.put("totalPnl", totalPnl);
        response.put("tradeCount", pnlDetails.size());
        response.put("details", pnlDetails);

        return ResponseEntity.ok(response);
    }

    /**
     * Get P&L for a specific date
     */
    @GetMapping("/pnl/{date}")
    public ResponseEntity<Map<String, Object>> getPnl(@PathVariable String date) {
        LocalDate pnlDate = LocalDate.parse(date);
        
        BigDecimal totalPnl = pnlAttributionService.getTotalPnl(pnlDate);
        List<PnlExplain> pnlDetails = pnlAttributionService.getPnlForDate(pnlDate);

        Map<String, Object> response = new HashMap<>();
        response.put("pnlDate", pnlDate);
        response.put("totalPnl", totalPnl);
        response.put("tradeCount", pnlDetails.size());
        response.put("details", pnlDetails);

        return ResponseEntity.ok(response);
    }

    /**
     * Get high unexplained P&L
     */
    @GetMapping("/pnl/{date}/unexplained")
    public ResponseEntity<List<PnlExplain>> getUnexplainedPnl(
            @PathVariable String date,
            @RequestParam(defaultValue = "1000") BigDecimal threshold) {
        LocalDate pnlDate = LocalDate.parse(date);
        return ResponseEntity.ok(pnlAttributionService.getHighUnexplainedPnl(pnlDate, threshold));
    }

    /**
     * Run scenario analysis
     */
    @PostMapping("/scenario")
    public ResponseEntity<Map<String, Object>> runScenario(@RequestBody ScenarioRequest request) {
        log.info("Running scenario: {}", request.getScenarioName());

        ValuationScenario scenario = scenarioService.runScenario(
            request.getScenarioName(),
            request.getScenarioType(),
            request.getBaseDate(),
            request.getParameters(),
            request.getPortfolioFilter(),
            request.getCreatedBy()
        );

        BigDecimal totalImpact = scenarioService.getTotalImpact(scenario.getScenarioId());
        List<ScenarioResult> topImpacts = scenarioService.getTopImpacts(scenario.getScenarioId());

        Map<String, Object> response = new HashMap<>();
        response.put("scenarioId", scenario.getScenarioId());
        response.put("scenarioName", scenario.getScenarioName());
        response.put("totalImpact", totalImpact);
        response.put("topImpacts", topImpacts);

        return ResponseEntity.ok(response);
    }

    /**
     * Get scenario results
     */
    @GetMapping("/scenario/{scenarioId}")
    public ResponseEntity<Map<String, Object>> getScenarioResults(@PathVariable Long scenarioId) {
        List<ScenarioResult> results = scenarioService.getScenarioResults(scenarioId);
        BigDecimal totalImpact = scenarioService.getTotalImpact(scenarioId);

        Map<String, Object> response = new HashMap<>();
        response.put("scenarioId", scenarioId);
        response.put("totalImpact", totalImpact);
        response.put("tradeCount", results.size());
        response.put("results", results);

        return ResponseEntity.ok(response);
    }

    // DTOs
    public static class BatchValuationRequest {
        private LocalDate valuationDate;
        private String portfolioFilter;
        private String startedBy;

        public LocalDate getValuationDate() { return valuationDate; }
        public void setValuationDate(LocalDate valuationDate) { this.valuationDate = valuationDate; }

        public String getPortfolioFilter() { return portfolioFilter; }
        public void setPortfolioFilter(String portfolioFilter) { this.portfolioFilter = portfolioFilter; }

        public String getStartedBy() { return startedBy; }
        public void setStartedBy(String startedBy) { this.startedBy = startedBy; }
    }

    public static class PnlCalculationRequest {
        private LocalDate pnlDate;

        public LocalDate getPnlDate() { return pnlDate; }
        public void setPnlDate(LocalDate pnlDate) { this.pnlDate = pnlDate; }
    }

    public static class ScenarioRequest {
        private String scenarioName;
        private String scenarioType;
        private LocalDate baseDate;
        private String parameters;
        private String portfolioFilter;
        private String createdBy;

        public String getScenarioName() { return scenarioName; }
        public void setScenarioName(String scenarioName) { this.scenarioName = scenarioName; }

        public String getScenarioType() { return scenarioType; }
        public void setScenarioType(String scenarioType) { this.scenarioType = scenarioType; }

        public LocalDate getBaseDate() { return baseDate; }
        public void setBaseDate(LocalDate baseDate) { this.baseDate = baseDate; }

        public String getParameters() { return parameters; }
        public void setParameters(String parameters) { this.parameters = parameters; }

        public String getPortfolioFilter() { return portfolioFilter; }
        public void setPortfolioFilter(String portfolioFilter) { this.portfolioFilter = portfolioFilter; }

        public String getCreatedBy() { return createdBy; }
        public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    }
}
