package com.trading.ctrm.pricing;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.trading.ctrm.instrument.Instrument;
import com.trading.ctrm.marketdata.MarketDataSnapshot;
import com.trading.ctrm.marketdata.MarketDataService;
import com.trading.ctrm.trade.Trade;
import com.trading.ctrm.rules.ValuationContext;

/**
 * Power Forward Pricing Engine - Endur-style with ValuationContext
 */
public class PowerForwardPricingEngine implements PricingEngine {

    private final MarketDataService marketDataService;

    public PowerForwardPricingEngine(MarketDataService marketDataService) {
        this.marketDataService = marketDataService;
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

        // Load market data
        MarketDataSnapshot snapshot = marketDataService.loadSnapshot();
        
        BigDecimal marketPrice = snapshot.getPrice(instrument.getInstrumentCode());

        if (marketPrice == null) {
            throw new IllegalStateException(
                    "No market price found for instrument: " +
                    instrument.getInstrumentCode()
            );
        }

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
                .pricingModel(pricingCtx != null ? pricingCtx.pricingModel() : "DCF")
                .build();
                
        result.setCalcDurationMs((int) duration);
        return result;
    }
}