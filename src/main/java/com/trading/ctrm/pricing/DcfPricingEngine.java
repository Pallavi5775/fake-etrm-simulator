package com.trading.ctrm.pricing;

import com.trading.ctrm.instrument.Instrument;
import com.trading.ctrm.trade.Trade;
import com.trading.ctrm.rules.ValuationContext;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DCF (Discounted Cash Flow) Pricing Engine
 * Uses context for cash flows and discount rate.
 */
public class DcfPricingEngine implements PricingEngine {
    
    private static final Logger log = LoggerFactory.getLogger(DcfPricingEngine.class);
    @Override
    public ValuationResult price(Trade trade, Instrument instrument, ValuationContext context) {
        log.info("[DcfPricingEngine] Starting DCF pricing for trade ID: {}, instrument: {}", 
            trade.getId(), instrument.getInstrumentCode());
        
        var pricingCtx = context.pricing();

        log.info(pricingCtx.cashFlows() != null 
            ? "[DcfPricingEngine] Pricing context has {} cash flows." 
            : "[DcfPricingEngine] Pricing context has no cash flows.");
        
        // Example: cash flows and times from context (should be List<BigDecimal> and List<Double> in years)
        List<BigDecimal> cashFlows = pricingCtx != null && pricingCtx.cashFlows() != null 
            ? pricingCtx.cashFlows() 
            : List.of(trade.getQuantity().multiply(trade.getPrice()));
        List<Double> times = pricingCtx != null && pricingCtx.cashFlowTimes() != null 
            ? pricingCtx.cashFlowTimes() 
            : List.of(1.0);
        double r = pricingCtx != null && pricingCtx.discountRate() != null 
            ? pricingCtx.discountRate().doubleValue() 
            : 0.05;

        log.info("[DcfPricingEngine] Using {} cash flows, discount rate: {}, times: {}", 
            cashFlows.size(), r, times);

        BigDecimal pv = BigDecimal.ZERO;
        for (int i = 0; i < cashFlows.size(); i++) {
            double t = i < times.size() ? times.get(i) : 1.0;
            BigDecimal cf = cashFlows.get(i);
            double df = Math.exp(-r * t);
            BigDecimal discountedCf = cf.multiply(BigDecimal.valueOf(df));
            pv = pv.add(discountedCf);
            
            log.info("[DcfPricingEngine] Cash flow {}: amount={}, time={}, df={}, discounted={}", 
                i, cf, t, df, discountedCf);
        }

        log.info("[DcfPricingEngine] Total present value: {}", pv);

        Map<String, BigDecimal> greeks = new HashMap<>();
        greeks.put("delta", trade.getQuantity()); // DCF is linear in quantity

        ValuationResult result = ValuationResult.builder()
            .mtmTotal(pv)
            .tradeId(trade.getId())
            .greeks(greeks)
            .build();

        // Serialize MarketContext to JSON for jsonb column (may be null now)
        try {
            ObjectMapper mapper = new ObjectMapper();
            if (context.market() != null) {
                String marketContextJson = mapper.writeValueAsString(context.market());
                result.setMarketContext(marketContextJson);
                log.info("[DcfPricingEngine] Successfully serialized market context to JSON");
            } else {
                result.setMarketContext(null);
                log.info("[DcfPricingEngine] Market context is null, setting as null");
            }
        } catch (Exception e) {
            // Fallback: set as null and log error
            result.setMarketContext(null);
            log.error("[DcfPricingEngine] Failed to serialize market context to JSON", e);
        }
        
        log.info("[DcfPricingEngine] Completed DCF pricing for trade ID: {}", trade.getId());
        return result;
    }
}
