package com.trading.ctrm.risk;

import com.trading.ctrm.trade.TradeVersion;
import com.trading.ctrm.trade.TradeVersioningService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Risk Controller - Phase 3 risk management endpoints
 */
@RestController
@RequestMapping("/api/risk")
public class RiskController {

    private static final Logger log = LoggerFactory.getLogger(RiskController.class);

    private final PositionService positionService;
    private final RiskLimitService riskLimitService;
    private final VarService varService;
    private final TradeVersioningService versioningService;

    public RiskController(
            PositionService positionService,
            RiskLimitService riskLimitService,
            VarService varService,
            TradeVersioningService versioningService) {
        this.positionService = positionService;
        this.riskLimitService = riskLimitService;
        this.varService = varService;
        this.versioningService = versioningService;
    }

    // ============================================================================
    // POSITION MANAGEMENT
    // ============================================================================

    @PostMapping("/positions/calculate")
    public ResponseEntity<Map<String, Object>> calculatePositions(@RequestBody PositionRequest request) {
        log.info("Calculating positions for date: {}", request.getPositionDate());

        positionService.calculatePositions(request.getPositionDate(), request.getPortfolioFilter());

        List<Position> positions = positionService.getPositions(request.getPositionDate());

        Map<String, Object> response = new HashMap<>();
        response.put("positionDate", request.getPositionDate());
        response.put("positionCount", positions.size());
        response.put("positions", positions);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/positions/{date}")
    public ResponseEntity<List<Position>> getPositions(@PathVariable String date) {
        LocalDate positionDate = LocalDate.parse(date);
        return ResponseEntity.ok(positionService.getPositions(positionDate));
    }

    @GetMapping("/positions/{date}/portfolio/{portfolio}")
    public ResponseEntity<Map<String, Object>> getPortfolioPositions(
            @PathVariable String date,
            @PathVariable String portfolio) {
        LocalDate positionDate = LocalDate.parse(date);
        
        List<Position> positions = positionService.getPortfolioPositions(portfolio, positionDate);
        BigDecimal netPosition = positionService.getPortfolioNetPosition(portfolio, positionDate);
        BigDecimal mtm = positionService.getPortfolioMtm(portfolio, positionDate);

        Map<String, Object> response = new HashMap<>();
        response.put("portfolio", portfolio);
        response.put("positionDate", positionDate);
        response.put("netPosition", netPosition);
        response.put("totalMtm", mtm);
        response.put("positions", positions);

        return ResponseEntity.ok(response);
    }

    // ============================================================================
    // RISK LIMITS
    // ============================================================================

    @PostMapping("/limits/check")
    public ResponseEntity<Map<String, Object>> checkLimits(@RequestBody LimitCheckRequest request) {
        log.info("Checking limits for portfolio: {}", request.getPortfolio());

        List<RiskLimitBreach> breaches = riskLimitService.checkLimits(
            request.getPortfolio(),
            request.getCheckDate()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("portfolio", request.getPortfolio());
        response.put("checkDate", request.getCheckDate());
        response.put("breachCount", breaches.size());
        response.put("breaches", breaches);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/limits")
    public ResponseEntity<List<RiskLimit>> getActiveLimits() {
        return ResponseEntity.ok(riskLimitService.getActiveLimits());
    }

    @PostMapping("/limits")
    public ResponseEntity<RiskLimit> createLimit(@RequestBody RiskLimit limit) {
        RiskLimit saved = riskLimitService.saveLimit(limit);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/breaches/active")
    public ResponseEntity<List<RiskLimitBreach>> getActiveBreaches() {
        return ResponseEntity.ok(riskLimitService.getActiveBreaches());
    }

    @GetMapping("/breaches/critical")
    public ResponseEntity<List<RiskLimitBreach>> getCriticalBreaches() {
        return ResponseEntity.ok(riskLimitService.getCriticalBreaches());
    }

    @PostMapping("/breaches/{breachId}/resolve")
    public ResponseEntity<Map<String, String>> resolveBreach(
            @PathVariable Long breachId,
            @RequestBody ResolveBreachRequest request) {
        riskLimitService.resolveBreach(breachId, request.getResolvedBy(), request.getNotes());
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "resolved");
        response.put("breachId", breachId.toString());
        
        return ResponseEntity.ok(response);
    }

    // ============================================================================
    // VAR CALCULATION
    // ============================================================================

    @PostMapping("/var/calculate")
    public ResponseEntity<Map<String, Object>> calculateVaR(@RequestBody VarRequest request) {
        log.info("Calculating VaR for portfolio: {}", request.getPortfolio());

        BigDecimal var = varService.calculateVaR(
            request.getPortfolio(),
            request.getValueDate(),
            request.getConfidenceLevel(),
            request.getHoldingPeriodDays()
        );

        BigDecimal cvar = varService.calculateCVaR(
            request.getPortfolio(),
            request.getValueDate(),
            request.getConfidenceLevel()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("portfolio", request.getPortfolio());
        response.put("valueDate", request.getValueDate());
        response.put("confidenceLevel", request.getConfidenceLevel());
        response.put("holdingPeriodDays", request.getHoldingPeriodDays());
        response.put("var", var);
        response.put("cvar", cvar);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/var/trade/{tradeId}")
    public ResponseEntity<Map<String, Object>> getMarginalVaR(
            @PathVariable Long tradeId,
            @RequestParam(defaultValue = "0.95") double confidenceLevel) {
        
        BigDecimal marginalVar = varService.calculateMarginalVaR(tradeId, LocalDate.now(), confidenceLevel);

        Map<String, Object> response = new HashMap<>();
        response.put("tradeId", tradeId);
        response.put("marginalVar", marginalVar);
        response.put("confidenceLevel", confidenceLevel);

        return ResponseEntity.ok(response);
    }

    // ============================================================================
    // TRADE VERSIONING
    // ============================================================================

    @GetMapping("/trades/{tradeId}/history")
    public ResponseEntity<List<TradeVersion>> getTradeHistory(@PathVariable Long tradeId) {
        return ResponseEntity.ok(versioningService.getTradeHistory(tradeId));
    }

    @GetMapping("/trades/{tradeId}/version/{versionNumber}")
    public ResponseEntity<TradeVersion> getTradeVersion(
            @PathVariable Long tradeId,
            @PathVariable Integer versionNumber) {
        return versioningService.getVersion(tradeId, versionNumber)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // ============================================================================
    // DTOs
    // ============================================================================

    public static class PositionRequest {
        private LocalDate positionDate;
        private String portfolioFilter;

        public LocalDate getPositionDate() { return positionDate; }
        public void setPositionDate(LocalDate positionDate) { this.positionDate = positionDate; }

        public String getPortfolioFilter() { return portfolioFilter; }
        public void setPortfolioFilter(String portfolioFilter) { this.portfolioFilter = portfolioFilter; }
    }

    public static class LimitCheckRequest {
        private String portfolio;
        private LocalDate checkDate;

        public String getPortfolio() { return portfolio; }
        public void setPortfolio(String portfolio) { this.portfolio = portfolio; }

        public LocalDate getCheckDate() { return checkDate; }
        public void setCheckDate(LocalDate checkDate) { this.checkDate = checkDate; }
    }

    public static class ResolveBreachRequest {
        private String resolvedBy;
        private String notes;

        public String getResolvedBy() { return resolvedBy; }
        public void setResolvedBy(String resolvedBy) { this.resolvedBy = resolvedBy; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public static class VarRequest {
        private String portfolio;
        private LocalDate valueDate;
        private double confidenceLevel = 0.95;
        private int holdingPeriodDays = 1;

        public String getPortfolio() { return portfolio; }
        public void setPortfolio(String portfolio) { this.portfolio = portfolio; }

        public LocalDate getValueDate() { return valueDate; }
        public void setValueDate(LocalDate valueDate) { this.valueDate = valueDate; }

        public double getConfidenceLevel() { return confidenceLevel; }
        public void setConfidenceLevel(double confidenceLevel) { this.confidenceLevel = confidenceLevel; }

        public int getHoldingPeriodDays() { return holdingPeriodDays; }
        public void setHoldingPeriodDays(int holdingPeriodDays) { this.holdingPeriodDays = holdingPeriodDays; }
    }
}
