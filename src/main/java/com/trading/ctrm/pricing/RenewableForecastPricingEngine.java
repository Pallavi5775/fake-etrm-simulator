package com.trading.ctrm.pricing;

import com.trading.ctrm.instrument.Instrument;
import com.trading.ctrm.trade.Trade;
import com.trading.ctrm.rules.ValuationContext;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * Renewable Forecast Pricing Engine
 * Uses average forecasted price from context for MTM calculation.
 */
public class RenewableForecastPricingEngine implements PricingEngine {
    @Override
    public ValuationResult price(Trade trade, Instrument instrument, ValuationContext context) {
        var marketCtx = context.market();
        List<BigDecimal> forecastPrices = marketCtx != null && marketCtx.forecastPrices() != null ? marketCtx.forecastPrices() : List.of(trade.getPrice());
        BigDecimal avgForecast = forecastPrices.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(forecastPrices.size()), BigDecimal.ROUND_HALF_UP);
        BigDecimal mtm = avgForecast.subtract(trade.getPrice()).multiply(trade.getQuantity());

        Map<String, BigDecimal> greeks = new HashMap<>();
        greeks.put("delta", trade.getQuantity());

        return ValuationResult.builder()
            .mtmTotal(mtm)
            .greeks(greeks)
            .build();
    }
}
