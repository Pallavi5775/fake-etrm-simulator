package com.trading.ctrm.pricing;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import com.trading.ctrm.instrument.Instrument;
import com.trading.ctrm.instrument.InstrumentType;
import com.trading.ctrm.trade.Trade;
import com.trading.ctrm.trade.EnumType.BuySell;
import com.trading.ctrm.rules.ValuationContext;
import com.trading.ctrm.rules.MarketContext;

/**
 * Power Forward Pricing Engine - Uses forward curves for pricing
 */
public class PowerForwardPricingEngine implements PricingEngine {

    private final MarketContext marketContext;

    public PowerForwardPricingEngine(MarketContext marketContext) {
        this.marketContext = marketContext;
    }

    @Override
    public ValuationResult price(
            Trade trade,
            Instrument instrument,
            ValuationContext context
    ) {
        long startTime = System.currentTimeMillis();

        // Extract contexts
        var marketCtx = context.market();
        var pricingCtx = context.pricing();
        var riskCtx = context.risk();

        // Use valuation date from context as delivery date
        LocalDate deliveryDate = context.valuationDate();

        // For quarterly forwards, adjust delivery date to quarter end
        if (instrument.getInstrumentType() == InstrumentType.POWER_FORWARD && 
            instrument.getInstrumentCode().contains("_Q")) {
            String forwardCode = instrument.getInstrumentCode();
            // Extract year from instrument code (e.g., POWER_BASELOAD_Q1_2025 -> 2025)
            String yearStr = forwardCode.substring(forwardCode.lastIndexOf("_") + 1);
            int year = Integer.parseInt(yearStr);
            
            if (forwardCode.contains("_Q1_")) {
                // Q1 uses March 1 (first day of last month of Q1)
                deliveryDate = LocalDate.of(year, 3, 1);
            } else if (forwardCode.contains("_Q2_")) {
                // Q2 uses June 1 (first day of last month of Q2)
                deliveryDate = LocalDate.of(year, 6, 1);
            } else if (forwardCode.contains("_Q3_")) {
                // Q3 uses September 1 (first day of last month of Q3)
                deliveryDate = LocalDate.of(year, 9, 1);
            } else if (forwardCode.contains("_Q4_")) {
                // Q4 uses December 1 (first day of last month of Q4)
                deliveryDate = LocalDate.of(year, 12, 1);
            }
        }

        // Use MarketContext to get forward curve price (handles option vs non-option routing)
        BigDecimal marketPrice = marketContext.getForwardCurve(instrument, deliveryDate);
        
        if (marketPrice == null) {
            throw new IllegalStateException(
                "No forward curve found for instrument: " + instrument.getInstrumentCode() 
                + " on " + deliveryDate
            );
        }

        // Calculate signed quantity based on buy/sell
        BigDecimal signedQty = trade.getBuySell() == BuySell.BUY 
            ? trade.getQuantity() 
            : trade.getQuantity().negate();

        // Calculate MTM components
        BigDecimal mtmTotal = marketPrice
                .subtract(trade.getPrice())
                .multiply(signedQty);

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