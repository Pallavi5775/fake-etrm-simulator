package com.trading.ctrm.pricing;

import com.trading.ctrm.instrument.Instrument;
import com.trading.ctrm.trade.Trade;
import com.trading.ctrm.rules.ValuationContext;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Black Option Pricing Engine (European Option on Futures)
 * Similar to Black-76 but no discounting since futures are already at fair value.
 */
public class BlackPricingEngine implements PricingEngine {
    @Override
    public ValuationResult price(Trade trade, Instrument instrument, ValuationContext context) {
        // Extract required data from context (Endur-style)
        var pricingCtx = context.pricing();
        var marketCtx = context.market();
        var riskCtx = context.risk();

        // For futures options, get futures price from underlying instrument
        Instrument pricingInstrument = instrument;
        LocalDate pricingDate = marketCtx.pricingDate();

        if (instrument instanceof com.trading.ctrm.instrument.CommodityOptionInstrument) {
            // For options, use expiry date as the pricing date
            com.trading.ctrm.instrument.CommodityOptionInstrument option =
                (com.trading.ctrm.instrument.CommodityOptionInstrument) instrument;
            if (option.getExpiryDate() != null) {
                pricingDate = option.getExpiryDate();
            }
            pricingInstrument = instrument; // Use option instrument for market data lookup
        }

        // Inputs for Black model (no discounting for futures)
        double F = marketCtx != null ?
            marketCtx.getForwardCurve(pricingInstrument, pricingDate) != null ?
                marketCtx.getForwardCurve(pricingInstrument, pricingDate).doubleValue() : 100.0 : 100.0;

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

        // For Black model, time to expiry in years
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

        // Black formula (similar to Black-76 but no discounting)
        double d1 = (Math.log(F / K) + 0.5 * sigma * sigma * T) / (sigma * Math.sqrt(T));
        double d2 = d1 - sigma * Math.sqrt(T);
        double Nd1 = normCdf(isCall ? d1 : -d1);
        double Nd2 = normCdf(isCall ? d2 : -d2);

        // For futures options, no discounting applied (unlike Black-76)
        double optionPrice = isCall ?
            (F * Nd1 - K * Nd2) :
            (K * (1 - Nd2) - F * (1 - Nd1));

        // Apply quantity
        optionPrice *= quantity;

        // Create valuation result
        ValuationResult result = new ValuationResult();
        result.setTradeId(trade.getId());
        result.setInstrumentId(instrument.getId());
        result.setPricingDate(context.valuationDate());
        result.setMtmTotal(BigDecimal.valueOf(optionPrice));
        result.setCurrency(instrument.getCurrency());

        // Add Greeks (simplified)
        Map<String, BigDecimal> greeks = new HashMap<>();
        greeks.put("delta", BigDecimal.valueOf(isCall ? Nd1 : Nd1 - 1));
        greeks.put("gamma", BigDecimal.valueOf(normPdf(d1) / (F * sigma * Math.sqrt(T))));
        greeks.put("vega", BigDecimal.valueOf(F * Math.sqrt(T) * normPdf(d1)));
        greeks.put("theta", BigDecimal.valueOf(-F * sigma * normPdf(d1) / (2 * Math.sqrt(T))));
        result.setGreeks(greeks);

        return result;
    }

    // Normal cumulative distribution function
    private double normCdf(double x) {
        return 0.5 * (1.0 + erf(x / Math.sqrt(2.0)));
    }

    // Normal probability density function
    private double normPdf(double x) {
        return Math.exp(-0.5 * x * x) / Math.sqrt(2 * Math.PI);
    }

    // Error function approximation
    private double erf(double x) {
        // Abramowitz and Stegun approximation
        double a1 =  0.254829592;
        double a2 = -0.284496736;
        double a3 =  1.421413741;
        double a4 = -1.453152027;
        double a5 =  1.061405429;
        double p  =  0.3275911;

        double sign = x < 0 ? -1 : 1;
        x = Math.abs(x);

        double t = 1.0 / (1.0 + p * x);
        double y = 1.0 - (((((a5 * t + a4) * t) + a3) * t + a2) * t + a1) * t * Math.exp(-x * x);

        return sign * y;
    }
}