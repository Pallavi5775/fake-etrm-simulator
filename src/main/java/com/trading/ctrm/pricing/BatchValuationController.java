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
     * Test endpoint to check if controller is working
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ok");
        response.put("message", "BatchValuationController is working");
        response.put("timestamp", java.time.LocalDateTime.now());
        return ResponseEntity.ok(response);
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
     * Get valuation results for a specific date
     */
    @GetMapping("/batch/results/{date}")
    public ResponseEntity<Map<String, Object>> getValuationResults(@PathVariable String date) {
        LocalDate valuationDate = LocalDate.parse(date);
        
        List<ValuationResult> results = batchValuationService.getValuationResultsForDate(valuationDate);
        
        Map<String, Object> response = new HashMap<>();
        response.put("valuationDate", valuationDate);
        response.put("resultCount", results.size());
        response.put("results", results);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get all valuation results (for debugging)
     */
    @GetMapping("/batch/results")
    public ResponseEntity<Map<String, Object>> getAllValuationResults() {
        List<ValuationResult> allResults = batchValuationService.getAllValuationResults();
        
        Map<String, Object> response = new HashMap<>();
        response.put("totalResults", allResults.size());
        response.put("results", allResults);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get valuation results for a specific trade
     */
    @GetMapping("/batch/results/trade/{tradeId}")
    public ResponseEntity<Map<String, Object>> getValuationResultsForTrade(@PathVariable Long tradeId) {
        List<ValuationResult> results = batchValuationService.getValuationResultsForTrade(tradeId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("tradeId", tradeId);
        response.put("resultCount", results.size());
        response.put("results", results);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get all trades (for debugging P&L issues)
     */
    @GetMapping("/trades")
    public ResponseEntity<Map<String, Object>> getAllTrades() {
        List<com.trading.ctrm.trade.Trade> trades = batchValuationService.getAllTrades();
        
        Map<String, Object> response = new HashMap<>();
        response.put("tradeCount", trades.size());
        response.put("trades", trades.stream().map(trade -> Map.of(
            "id", trade.getId(),
            "tradeId", trade.getTradeId(),
            "instrumentCode", trade.getInstrument() != null ? trade.getInstrument().getInstrumentCode() : null,
            "status", trade.getStatus(),
            "tradeDate", trade.getTradeDate()
        )).collect(java.util.stream.Collectors.toList()));
        
        return ResponseEntity.ok(response);
    }

    /**
     * Calculate daily P&L
     */
    @PostMapping("/pnl/calculate")
    public ResponseEntity<Map<String, Object>> calculatePnl(@RequestBody(required = false) PnlCalculationRequest request) {
        try {
            // Default to today if request is null or pnlDate is null
            LocalDate pnlDate = LocalDate.now();
            if (request != null && request.getPnlDate() != null) {
                pnlDate = request.getPnlDate();
            }
            
            log.info("Calculating P&L for date: {}", pnlDate);

            pnlAttributionService.calculateDailyPnl(pnlDate);

            BigDecimal totalPnl = pnlAttributionService.getTotalPnl(pnlDate);
            List<PnlExplain> pnlDetails = pnlAttributionService.getPnlForDate(pnlDate);

            Map<String, Object> response = new HashMap<>();
            response.put("pnlDate", pnlDate);
            response.put("totalPnl", totalPnl);
            response.put("tradeCount", pnlDetails.size());
            response.put("details", pnlDetails);
            response.put("status", "success");

            log.info("P&L calculation completed for {} trades, total P&L: {}", pnlDetails.size(), totalPnl);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to calculate P&L", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("errorType", e.getClass().getSimpleName());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Calculate daily P&L (simple GET version for testing)
     */
    @GetMapping("/pnl/calculate/today")
    public ResponseEntity<Map<String, Object>> calculatePnlToday() {
        try {
            LocalDate pnlDate = LocalDate.now();
            log.info("Calculating P&L for today: {}", pnlDate);

            pnlAttributionService.calculateDailyPnl(pnlDate);

            BigDecimal totalPnl = pnlAttributionService.getTotalPnl(pnlDate);
            List<PnlExplain> pnlDetails = pnlAttributionService.getPnlForDate(pnlDate);

            Map<String, Object> response = new HashMap<>();
            response.put("pnlDate", pnlDate);
            response.put("totalPnl", totalPnl);
            response.put("tradeCount", pnlDetails.size());
            response.put("details", pnlDetails);
            response.put("status", "success");

            log.info("P&L calculation completed for {} trades, total P&L: {}", pnlDetails.size(), totalPnl);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to calculate P&L", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("errorType", e.getClass().getSimpleName());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Get P&L for a specific date (calculates if not exists)
     */
    @GetMapping("/pnl/{date}")
    public ResponseEntity<Map<String, Object>> getPnl(@PathVariable String date) {
        LocalDate pnlDate = LocalDate.parse(date);
        
        // Check if P&L already exists for this date
        List<PnlExplain> existingPnl = pnlAttributionService.getPnlForDate(pnlDate);
        if (existingPnl.isEmpty()) {
            log.info("No P&L data found for date {}, calculating now...", pnlDate);
            pnlAttributionService.calculateDailyPnl(pnlDate);
        }
        
        BigDecimal totalPnl = pnlAttributionService.getTotalPnl(pnlDate);
        List<PnlExplain> pnlDetails = pnlAttributionService.getPnlForDate(pnlDate);

        Map<String, Object> response = new HashMap<>();
        response.put("pnlDate", pnlDate);
        response.put("totalPnl", totalPnl);
        response.put("realizedPnl", pnlAttributionService.getRealizedPnl(pnlDate));
        response.put("unrealizedPnl", pnlAttributionService.getUnrealizedPnl(pnlDate));
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
     * Get top P&L performers for a date
     */
    @GetMapping("/pnl/{date}/top-performers")
    public ResponseEntity<Map<String, Object>> getTopPerformers(
            @PathVariable String date,
            @RequestParam(defaultValue = "10") int limit) {
        LocalDate pnlDate = LocalDate.parse(date);

        List<PnlExplain> topWinners = pnlAttributionService.getTopWinners(pnlDate, limit);
        List<PnlExplain> topLosers = pnlAttributionService.getTopLosers(pnlDate, limit);

        Map<String, Object> response = new HashMap<>();
        response.put("pnlDate", pnlDate);
        response.put("topWinners", topWinners);
        response.put("topLosers", topLosers);
        response.put("limit", limit);

        return ResponseEntity.ok(response);
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
