package com.trading.ctrm.pricing;

import com.trading.ctrm.instrument.Instrument;
import com.trading.ctrm.trade.Trade;
import com.trading.ctrm.rules.ValuationContext;
import java.math.BigDecimal;
import java.time.LocalDate;
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

        // For options, get forward price from underlying instrument
        Instrument pricingInstrument = instrument;
        LocalDate forwardDate = marketCtx.pricingDate();
        
        if (instrument instanceof com.trading.ctrm.instrument.CommodityOptionInstrument) {
            // For options, use expiry date as the forward date
            com.trading.ctrm.instrument.CommodityOptionInstrument option = 
                (com.trading.ctrm.instrument.CommodityOptionInstrument) instrument;
            if (option.getExpiryDate() != null) {
                forwardDate = option.getExpiryDate();
            }
            // For options, we need the underlying forward price
            // For now, try to find a forward instrument with similar code
            String optionCode = instrument.getInstrumentCode();
            // Simple heuristic: replace OPTION with FORWARD and remove CALL/PUT
            String forwardCode = optionCode.replace("OPTION", "FORWARD").replace("_CALL", "").replace("_PUT", "");
            // Try to find the forward instrument in the database via market context
            // For now, assume the forward curve uses the same instrument code pattern
            pricingInstrument = instrument; // Fallback to option instrument
        }

        // Inputs (should be provided in context/pricing config)
        double F = marketCtx != null ? 
            marketCtx.getForwardCurve(pricingInstrument, forwardDate) != null ? 
                marketCtx.getForwardCurve(pricingInstrument, forwardDate).doubleValue() : 100.0 : 100.0;
        // Get strike price from option instrument or trade price
        double K = 100.0; // default
        if (instrument instanceof com.trading.ctrm.instrument.CommodityOptionInstrument) {
            com.trading.ctrm.instrument.CommodityOptionInstrument option = 
                (com.trading.ctrm.instrument.CommodityOptionInstrument) instrument;
            if (option.getStrikePrice() != null) {
                K = option.getStrikePrice().doubleValue();
            }
        }
        if (K == 100.0 && trade.getPrice() != null) {
            K = trade.getPrice().doubleValue(); // fallback to trade price if no strike
        }
        double sigma = marketCtx != null ? marketCtx.getVolatility(instrument, marketCtx.pricingDate()) != null ? 
            Math.max(0.001, marketCtx.getVolatility(instrument, marketCtx.pricingDate()).doubleValue()) : 0.2 : 0.2;
        double r = marketCtx != null ? marketCtx.getYieldCurve(instrument, marketCtx.pricingDate()) != null ? 
            marketCtx.getYieldCurve(instrument, marketCtx.pricingDate()).doubleValue() : 0.05 : 0.05;
        double T = Math.max(0.001, pricingCtx != null && pricingCtx.yearsToExpiry() != null ? pricingCtx.yearsToExpiry() : 1.0);
        // Determine if it's a call or put option
        boolean isCall = true; // default to call
        if (instrument instanceof com.trading.ctrm.instrument.CommodityOptionInstrument) {
            com.trading.ctrm.instrument.CommodityOptionInstrument option = 
                (com.trading.ctrm.instrument.CommodityOptionInstrument) instrument;
            if (option.getOptionType() != null) {
                isCall = "CALL".equalsIgnoreCase(option.getOptionType());
            }
        }
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
