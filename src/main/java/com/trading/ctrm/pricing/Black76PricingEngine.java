package com.trading.ctrm.pricing;

import com.trading.ctrm.instrument.Instrument;
import com.trading.ctrm.trade.Trade;
import com.trading.ctrm.rules.ValuationContext;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Black76 Option Pricing Engine (European Option on Forward)
 * Uses context for volatility, rate, expiry, and forward price.
 */
public class Black76PricingEngine implements PricingEngine {
    @Override
    public ValuationResult price(Trade trade, Instrument instrument, ValuationContext context) {
        // Extract required data from context (Endur-style)
        var pricingCtx = context.pricing();
        var marketCtx = context.market();
        var riskCtx = context.risk();

        // Inputs (should be provided in context/pricing config)
        double F = pricingCtx != null && pricingCtx.forwardPrice() != null ? pricingCtx.forwardPrice().doubleValue() : 100.0;
        double K = trade.getPrice() != null ? trade.getPrice().doubleValue() : 100.0;
        double sigma = pricingCtx != null && pricingCtx.volatility() != null ? pricingCtx.volatility().doubleValue() : 0.2;
        double r = pricingCtx != null && pricingCtx.discountRate() != null ? pricingCtx.discountRate().doubleValue() : 0.05;
        double T = pricingCtx != null && pricingCtx.yearsToExpiry() != null ? pricingCtx.yearsToExpiry() : 1.0;
        boolean isCall = trade.getBuySell() != null && trade.getBuySell().name().equalsIgnoreCase("BUY");
        double quantity = trade.getQuantity() != null ? trade.getQuantity().doubleValue() : 1.0;

        // Black76 formula
        double d1 = (Math.log(F / K) + 0.5 * sigma * sigma * T) / (sigma * Math.sqrt(T));
        double d2 = d1 - sigma * Math.sqrt(T);
        double Nd1 = normCdf(isCall ? d1 : -d1);
        double Nd2 = normCdf(isCall ? d2 : -d2);
        double price = Math.exp(-r * T) * (isCall ? (F * Nd1 - K * Nd2) : (K * Nd2 - F * Nd1));
        BigDecimal mtm = BigDecimal.valueOf(price * quantity);

        // Greeks (Delta, Gamma, Vega)
        Map<String, BigDecimal> greeks = new HashMap<>();
        double delta = Math.exp(-r * T) * (isCall ? Nd1 : (Nd1 - 1));
        double gamma = Math.exp(-r * T) * normPdf(d1) / (F * sigma * Math.sqrt(T));
        double vega = Math.exp(-r * T) * F * normPdf(d1) * Math.sqrt(T) / 100.0; // per 1% vol
        greeks.put("delta", BigDecimal.valueOf(delta * quantity));
        greeks.put("gamma", BigDecimal.valueOf(gamma * quantity));
        greeks.put("vega", BigDecimal.valueOf(vega * quantity));

        return ValuationResult.builder()
                .mtmTotal(mtm)
                .greeks(greeks)
                .build();
    }

    // Standard normal CDF
    private static double normCdf(double x) {
        return 0.5 * (1.0 + erf(x / Math.sqrt(2.0)));
    }

    // Standard normal PDF
    private static double normPdf(double x) {
        return Math.exp(-0.5 * x * x) / Math.sqrt(2 * Math.PI);
    }

    // Error function approximation
    private static double erf(double z) {
        double t = 1.0 / (1.0 + 0.5 * Math.abs(z));
        double ans = 1 - t * Math.exp(-z * z - 1.26551223 +
                t * (1.00002368 +
                        t * (0.37409196 +
                                t * (0.09678418 +
                                        t * (-0.18628806 +
                                                t * (0.27886807 +
                                                        t * (-1.13520398 +
                                                                t * (1.48851587 +
                                                                        t * (-0.82215223 +
                                                                                t * 0.17087277)))))))));
        return z >= 0 ? ans : -ans;
    }
}
