package com.trading.ctrm.trade;

import java.time.LocalDate;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trading.ctrm.pricing.PricingService;


@RestController
@RequestMapping("/api/pricing")
@CrossOrigin(origins = "*")
public class PricingController {

    private final PricingService pricingService;
    private final TradeRepository tradeRepo;

    public PricingController(
            PricingService pricingService,
            TradeRepository tradeRepo) {
        this.pricingService = pricingService;
        this.tradeRepo = tradeRepo;
    }

    @GetMapping("/mtm/{tradeId}")
    public double mtm(@PathVariable String tradeId) {
        var trade = tradeRepo.findByTradeId(tradeId)
                .orElseThrow(() ->
                        new RuntimeException("Trade not found: " + tradeId));

        return pricingService.calculateMTM(
                trade, LocalDate.now());
    }
}
