package com.trading.ctrm.rules;

import com.trading.ctrm.trade.Trade;

public class RiskContext {

    private final String evaluationPurpose;   // RISK / PNL / SIMULATION
    private final boolean greeksEnabled;
    private final String shockScenario;
    private final String aggregationLevel;

    public RiskContext(
            String evaluationPurpose,
            boolean greeksEnabled,
            String shockScenario,
            String aggregationLevel
    ) {
        this.evaluationPurpose = evaluationPurpose;
        this.greeksEnabled = greeksEnabled;
        this.shockScenario = shockScenario;
        this.aggregationLevel = aggregationLevel;
    }

    public String evaluationPurpose() { return evaluationPurpose; }
    public boolean greeksEnabled() { return greeksEnabled; }
    public String shockScenario() { return shockScenario; }
    public String aggregationLevel() { return aggregationLevel; }

    public static RiskContext of(
            String evaluationPurpose,
            boolean greeksEnabled,
            String shockScenario,
            String aggregationLevel
    ) {
        return new RiskContext(
                evaluationPurpose,
                greeksEnabled,
                shockScenario,
                aggregationLevel
        );
    }

    /**
     * Factory method to create RiskContext from a Trade entity with default risk settings
     */
    public static RiskContext fromTrade(Trade trade) {
        boolean hasOptions = trade.getInstrument().getInstrumentType().name().contains("OPTION");
        
        return new RiskContext(
                "RISK",
                hasOptions,
                "DELTA_1BP",
                "PORTFOLIO"
        );
    }

    /**
     * Factory method with configurable parameters from UI
     */
    public static RiskContext fromTrade(
            Trade trade,
            String evaluationPurpose,
            Boolean greeksEnabled,
            String shockScenario,
            String aggregationLevel
    ) {
        boolean hasOptions = trade.getInstrument().getInstrumentType().name().contains("OPTION");
        
        return new RiskContext(
                evaluationPurpose != null ? evaluationPurpose : "RISK",
                greeksEnabled != null ? greeksEnabled : hasOptions,
                shockScenario != null ? shockScenario : "DELTA_1BP",
                aggregationLevel != null ? aggregationLevel : "PORTFOLIO"
        );
    }
}
