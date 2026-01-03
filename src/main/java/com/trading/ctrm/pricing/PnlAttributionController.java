package com.trading.ctrm.pricing;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
     * Get P&L attribution breakdown for a given date
     */
    @GetMapping("/{date}/attribution")
    public ResponseEntity<Map<String, Object>> getPnlAttribution(@PathVariable String date) {
        LocalDate pnlDate = LocalDate.parse(date);
        // For now, just return the explained/unexplained breakdown for all trades
        var details = pnlAttributionService.getPnlForDate(pnlDate);
        var totalPnl = pnlAttributionService.getTotalPnl(pnlDate);
        Map<String, Object> response = new HashMap<>();
        response.put("pnlDate", pnlDate);
        response.put("totalPnl", totalPnl);
        response.put("details", details);
        return ResponseEntity.ok(response);
    }
}
