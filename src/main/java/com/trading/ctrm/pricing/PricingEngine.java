package com.trading.ctrm.pricing;

import com.trading.ctrm.instrument.Instrument;
import com.trading.ctrm.trade.Trade;
import com.trading.ctrm.rules.ValuationContext;

/**
 * Pricing Engine Interface - Endur-style with ValuationContext
 * 
 * Now accepts rich context for:
 * - Market data selection (curves, surfaces)
 * - Pricing model configuration
 * - Risk calculation preferences
 * - Accounting treatment
 */
public interface PricingEngine {

    /**
     * Calculate comprehensive valuation for a trade using full context
     *
     * @param trade      Trade instance
     * @param instrument Instrument definition
     * @param context    ValuationContext (market, pricing, risk, accounting)
     * @return ValuationResult with MTM, Greeks, and risk metrics
     */
    ValuationResult price(
            Trade trade,
            Instrument instrument,
            ValuationContext context
    );
}
