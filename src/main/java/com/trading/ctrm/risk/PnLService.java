package com.trading.ctrm.risk;

import org.springframework.stereotype.Service;

import com.trading.ctrm.pricing.ValuationHistory;
import com.trading.ctrm.pricing.ValuationHistoryRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PnLService {

    private final ValuationHistoryRepository valuationHistoryRepository;

    public PnLService(ValuationHistoryRepository valuationHistoryRepository) {
        this.valuationHistoryRepository = valuationHistoryRepository;
    }

    public BigDecimal calculatePnL(Long tradeId) {

        List<ValuationHistory> history =
                valuationHistoryRepository.findLatestTwo(tradeId);

        if (history.size() < 2) {
            return BigDecimal.ZERO;
        }

        BigDecimal latest = history.get(0).getMtm();
        BigDecimal previous = history.get(1).getMtm();

        return latest.subtract(previous);
    }
}