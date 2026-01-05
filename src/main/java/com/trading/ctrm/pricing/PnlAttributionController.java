package com.trading.ctrm.pricing;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;

/**
 * P&L Attribution Controller - provides attribution breakdown for a given date
 */
@RestController
@RequestMapping("/api/valuation/pnl")
public class PnlAttributionController {

    private static final Logger log = LoggerFactory.getLogger(PnlAttributionController.class);
    private final PnlAttributionService pnlAttributionService;

    public PnlAttributionController(PnlAttributionService pnlAttributionService) {
        this.pnlAttributionService = pnlAttributionService;
    }

    /**
     * Calculate daily P&L for all trades
     */
    @PostMapping("/daily-calculate")
    public ResponseEntity<String> calculateDailyPnl() {
        try {
            pnlAttributionService.calculateDailyPnl(LocalDate.now());
            return ResponseEntity.ok("Daily P&L calculation completed successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to calculate P&L: " + e.getMessage());
        }
    }
}
