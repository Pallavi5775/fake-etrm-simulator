package com.trading.ctrm.credit;

import org.springframework.stereotype.Service;

import com.trading.ctrm.trade.CreditLimit;
import com.trading.ctrm.trade.CreditLimitRepository;
import com.trading.ctrm.trade.Trade;

@Service
public class CreditService {

    private final CreditLimitRepository limitRepo;

    public CreditService(CreditLimitRepository limitRepo) {
        this.limitRepo = limitRepo;
    }

    public void checkCredit(Trade trade, double marketPrice) {

        CreditLimit limit = limitRepo
                .findByCounterparty(trade.getCounterparty())
                .orElseThrow(() ->
                        new RuntimeException("No credit limit set"));

        double exposure = trade.getQuantity() * marketPrice;

        if (exposure > limit.getLimitAmount()) {
            throw new RuntimeException(
                    "CREDIT LIMIT BREACH for " +
                            trade.getCounterparty());
        }
    }
}
