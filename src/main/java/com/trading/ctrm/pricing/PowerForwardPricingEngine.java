package com.trading.ctrm.pricing;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import com.trading.ctrm.instrument.Instrument;
import com.trading.ctrm.trade.ForwardCurve;
import com.trading.ctrm.trade.ForwardCurveRepository;
import com.trading.ctrm.trade.Trade;
import com.trading.ctrm.rules.ValuationContext;

/**
 * Power Forward Pricing Engine - Uses forward curves for pricing
 */
public class PowerForwardPricingEngine implements PricingEngine {

    private final ForwardCurveRepository forwardCurveRepository;

    public PowerForwardPricingEngine(ForwardCurveRepository forwardCurveRepository) {
        this.forwardCurveRepository = forwardCurveRepository;
    }

    @Override
    public ValuationResult price(
            Trade trade,
            Instrument instrument,
            ValuationContext context
    ) {
        long startTime = System.currentTimeMillis();

        // Extract contexts
        var pricingCtx = context.pricing();
        var riskCtx = context.risk();

        // Use trade date as delivery date (or today if not set)
        LocalDate deliveryDate = trade.getTradeDate() != null 
            ? trade.getTradeDate() 
            : LocalDate.now();

        // Load forward curve price
        ForwardCurve curve = forwardCurveRepository
            .findLatestByInstrumentAndDeliveryDate(instrument, deliveryDate)
            .orElseThrow(() -> new IllegalStateException(
                "No forward curve found for instrument: " + instrument.getInstrumentCode() 
                + " on " + deliveryDate
            ));

        BigDecimal marketPrice = BigDecimal.valueOf(curve.getPrice());

        // Calculate MTM components
        BigDecimal mtmTotal = marketPrice
                .subtract(trade.getPrice())
                .multiply(trade.getQuantity());

        // Calculate Greeks if requested
        Map<String, BigDecimal> greeks = new HashMap<>();
        if (riskCtx != null && riskCtx.greeksEnabled()) {
            greeks.put("delta", trade.getQuantity()); // Delta = position size for linear
            greeks.put("gamma", BigDecimal.ZERO);
            greeks.put("vega", BigDecimal.ZERO);
            greeks.put("theta", BigDecimal.ZERO);
            greeks.put("rho", BigDecimal.ZERO);
        }

        long duration = System.currentTimeMillis() - startTime;

        // Build comprehensive result
        ValuationResult result = ValuationResult.builder()
                .tradeId(trade.getId())
                .mtmTotal(mtmTotal)
                .mtmComponents(BigDecimal.ZERO, mtmTotal, BigDecimal.ZERO)
                .greeks(greeks)
                .pricingModel(pricingCtx != null ? pricingCtx.pricingModel() : "ForwardCurve")
                .build();
                
        result.setCalcDurationMs((int) duration);
        return result;
    }
}