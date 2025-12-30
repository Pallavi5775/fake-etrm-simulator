package com.trading.ctrm.credit;

import java.math.BigDecimal;

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

    public void checkCredit(Trade trade, BigDecimal marketPrice) {

        CreditLimit limit = limitRepo
                .findByCounterparty(trade.getCounterparty())
                .orElseThrow(() ->
                        new RuntimeException("No credit limit set"));

        BigDecimal exposure = trade.getQuantity().multiply(marketPrice);

        if (exposure.compareTo(limit.getLimitAmount()) > 0) {
    throw new RuntimeException(
            "CREDIT LIMIT BREACH for " + trade.getCounterparty()
    );
}
    }
}
