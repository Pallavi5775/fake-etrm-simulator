package com.trading.ctrm.trade;

import java.time.LocalDate;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.trading.ctrm.pricing.PricingService;

@Component
public class EndOfDayPnLJob {

    private final TradeRepository tradeRepo;
    private final PricingService pricingService;

    public EndOfDayPnLJob(
            TradeRepository tradeRepo,
            PricingService pricingService) {
        this.tradeRepo = tradeRepo;
        this.pricingService = pricingService;
    }

    @Scheduled(cron = "0 0 23 * * ?")
    public void run() {
        tradeRepo.findAll().forEach(trade -> {
            double mtm = pricingService
                .calculateMTM(trade, LocalDate.now());
            System.out.println("Trade " + trade.getTradeId() +
                               " MTM=" + mtm);
        });
    }
}
