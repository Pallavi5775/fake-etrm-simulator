package com.trading.ctrm.pricing;

import com.trading.ctrm.instrument.Instrument;
import com.trading.ctrm.trade.Trade;
import com.trading.ctrm.rules.ValuationContext;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * DCF (Discounted Cash Flow) Pricing Engine
 * Uses context for cash flows and discount rate.
 */
public class DcfPricingEngine implements PricingEngine {
    @Override
    public ValuationResult price(Trade trade, Instrument instrument, ValuationContext context) {
        var pricingCtx = context.pricing();
        // Example: cash flows and times from context (should be List<BigDecimal> and List<Double> in years)
        List<BigDecimal> cashFlows = pricingCtx != null && pricingCtx.cashFlows() != null ? pricingCtx.cashFlows() : List.of(trade.getQuantity().multiply(trade.getPrice()));
        List<Double> times = pricingCtx != null && pricingCtx.cashFlowTimes() != null ? pricingCtx.cashFlowTimes() : List.of(1.0);
        double r = pricingCtx != null && pricingCtx.discountRate() != null ? pricingCtx.discountRate().doubleValue() : 0.05;

        BigDecimal pv = BigDecimal.ZERO;
        for (int i = 0; i < cashFlows.size(); i++) {
            double t = i < times.size() ? times.get(i) : 1.0;
            BigDecimal cf = cashFlows.get(i);
            double df = Math.exp(-r * t);
            pv = pv.add(cf.multiply(BigDecimal.valueOf(df)));
        }

        Map<String, BigDecimal> greeks = new HashMap<>();
        greeks.put("delta", trade.getQuantity()); // DCF is linear in quantity

        return ValuationResult.builder()
            .mtmTotal(pv)
            .greeks(greeks)
            .build();
    }
}
