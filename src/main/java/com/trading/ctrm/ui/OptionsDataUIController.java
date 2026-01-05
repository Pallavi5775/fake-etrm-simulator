package com.trading.ctrm.ui;

import com.trading.ctrm.pricing.OptionsMarketDataLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Options Data Management (UI)
 * Provides endpoints for loading and managing options market data
 */
@RestController
@RequestMapping("/api/ui/options")
@CrossOrigin(origins = "*")
public class OptionsDataUIController {

    @Autowired
    private OptionsMarketDataLoader optionsMarketDataLoader;

    /**
     * Load comprehensive options market data template
     * Creates commodities, instruments, trades, and market data
     */
    @PostMapping("/load-template-data")
    public ResponseEntity<String> loadTemplateData() {
        try {
            optionsMarketDataLoader.loadSampleData();
            return ResponseEntity.ok("Options market data template loaded successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error loading options data: " + e.getMessage());
        }
    }

    /**
     * Health check endpoint for options data service
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Options Data Service is running");
    }
}