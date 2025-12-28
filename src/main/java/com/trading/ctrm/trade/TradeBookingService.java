package com.trading.ctrm.trade;
import com.trading.ctrm.credit.CreditService;
import org.springframework.stereotype.Service;

@Service
public class TradeBookingService {

    private final TradeRepository repo;
    private final CreditService creditService;

    public TradeBookingService(
            TradeRepository repo,
            CreditService creditService) {
        this.repo = repo;
        this.creditService = creditService;
    }

    public Trade book(Trade trade) {

        // 1️⃣ Pre-deal credit check (FO control)
        creditService.checkCredit(trade, trade.getPrice());

        // 2️⃣ Initial lifecycle state
        trade.setStatus(TradeStatus.CREATED);

        // 3️⃣ Persist trade
        return repo.save(trade);
    }
}

