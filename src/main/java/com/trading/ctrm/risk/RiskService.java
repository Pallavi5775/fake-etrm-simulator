package com.trading.ctrm.risk;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import com.trading.ctrm.trade.Trade;

@Service
public class RiskService {

   public BigDecimal exposure(Trade trade, BigDecimal marketPrice) {
    return trade.getQuantity().multiply(marketPrice);
}

    public BigDecimal creditExposure(
        Trade trade,
        BigDecimal marketPrice,
        BigDecimal ratingFactor
) {
    return exposure(trade, marketPrice)
            .multiply(ratingFactor)
            .setScale(2, RoundingMode.HALF_UP);
}
}

