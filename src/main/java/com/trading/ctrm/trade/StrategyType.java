package com.trading.ctrm.trade;

public enum StrategyType {
    // Single leg
    OUTRIGHT,
    
    // Two-leg spreads
    CALENDAR_SPREAD,      // Buy near month, sell far month
    INTER_COMMODITY,      // Spread between different commodities
    LOCATION_SPREAD,      // Same commodity, different locations
    
    // Three-leg spreads
    BUTTERFLY,            // 1x2x1 ratio
    CONDOR,              // 1x1x1x1 ratio
    CRACK_SPREAD,        // Oil refining (crude, gas, diesel)
    
    // Options
    STRADDLE,            // Buy call + buy put same strike
    STRANGLE,            // Buy call + buy put different strikes
    BULL_CALL_SPREAD,
    BEAR_PUT_SPREAD,
    IRON_CONDOR,
    
    // Custom
    CUSTOM
}
