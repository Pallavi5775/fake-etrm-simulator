package com.trading.ctrm.pricing;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * REST Controller for Options Market Data Management
 */
@RestController
@RequestMapping("/api/options")
public class OptionsDataController {

    @Autowired
    private OptionsMarketDataLoader dataLoader;

    @Autowired
    private OptionForwardCurveRepository forwardCurveRepository;

    @Autowired
    private OptionVolatilityRepository volatilityRepository;

    @Autowired
    private OptionYieldCurveRepository yieldCurveRepository;

    @Autowired
    private DatabaseAdminService databaseAdminService;

    /**
     * Load sample market data for options
     * POST /api/options/load-sample-data
     */
    @PostMapping("/load-sample-data")
    public ResponseEntity<String> loadSampleData() {
        try {
            dataLoader.loadSampleData();
            return ResponseEntity.ok("Sample options market data loaded successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error loading sample data: " + e.getMessage());
        }
    }

    /**
     * Get data summary for options
     * GET /api/options/data-summary
     */
    @GetMapping("/data-summary")
    public ResponseEntity<OptionsDataSummary> getDataSummary() {
        OptionsDataSummary summary = new OptionsDataSummary();
        summary.setForwardCurvesCount(forwardCurveRepository.count());
        summary.setVolatilityCount(volatilityRepository.count());
        summary.setYieldCurvesCount(yieldCurveRepository.count());

        return ResponseEntity.ok(summary);
    }

    /**
     * ADMIN ENDPOINT: Truncate all database tables
     * WARNING: This will delete ALL data permanently!
     * POST /api/options/admin/truncate-database?confirm=YES
     */
    @PostMapping("/admin/truncate-database")
    public ResponseEntity<String> truncateDatabase(@RequestParam String confirm) {
        if (!"YES".equals(confirm)) {
            return ResponseEntity.badRequest()
                .body("Confirmation required. Add ?confirm=YES to proceed.");
        }

        try {
            databaseAdminService.truncateAllTables();
            return ResponseEntity.ok("Database truncated successfully. All data has been deleted and sequences reset.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error truncating database: " + e.getMessage());
        }
    }

    /**
     * Data summary response object
     */
    public static class OptionsDataSummary {
        private long forwardCurvesCount;
        private long volatilityCount;
        private long yieldCurvesCount;

        // Getters and setters
        public long getForwardCurvesCount() { return forwardCurvesCount; }
        public void setForwardCurvesCount(long forwardCurvesCount) { this.forwardCurvesCount = forwardCurvesCount; }

        public long getVolatilityCount() { return volatilityCount; }
        public void setVolatilityCount(long volatilityCount) { this.volatilityCount = volatilityCount; }

        public long getYieldCurvesCount() { return yieldCurvesCount; }
        public void setYieldCurvesCount(long yieldCurvesCount) { this.yieldCurvesCount = yieldCurvesCount; }
    }
}