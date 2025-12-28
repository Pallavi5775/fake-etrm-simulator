package com.trading.ctrm.lifestyle;

import java.util.List;

public class SimulationResult {

    private String tradeId;
    private String finalStatus;
    private List<RuleExecutionTrace> traces;
    private List<String> warnings;

    // -------- Getters & Setters --------

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getFinalStatus() {
        return finalStatus;
    }

    public void setFinalStatus(String finalStatus) {
        this.finalStatus = finalStatus;
    }

    public List<RuleExecutionTrace> getTraces() {
        return traces;
    }

    public void setTraces(List<RuleExecutionTrace> traces) {
        this.traces = traces;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    // -------- Factory Methods --------

    // ✅ ALLOWED case
    public static SimulationResult allowed(Enum<?> nextStatus) {
        SimulationResult r = new SimulationResult();
        r.setFinalStatus(nextStatus.name());
        r.setWarnings(List.of());
        r.setTraces(List.of());
        return r;
    }

    // ❌ REJECTED case
    public static SimulationResult rejected(String reason) {
        SimulationResult r = new SimulationResult();
        r.setFinalStatus("REJECTED");
        r.setWarnings(List.of(reason));
        r.setTraces(List.of());
        return r;
    }
}
