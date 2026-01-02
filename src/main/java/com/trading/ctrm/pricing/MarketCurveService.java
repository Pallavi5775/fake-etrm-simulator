package com.trading.ctrm.pricing;

import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class MarketCurveService {

    private final MarketCurveRepository repository;

    public MarketCurveService(MarketCurveRepository repository) {
        this.repository = repository;
    }

    public MarketCurve getCurve(String curveName, LocalDate pricingDate) {
        return repository.findByCurveNameAndDate(curveName, pricingDate)
                .or(() -> repository.findLatestCurve(curveName, pricingDate))
                .orElseThrow(() -> new IllegalArgumentException(
                    "No market curve found for: " + curveName + " on " + pricingDate
                ));
    }
}
