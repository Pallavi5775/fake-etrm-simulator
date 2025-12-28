package com.trading.ctrm.lifestyle;

import java.util.List;
import java.util.Map;

import com.trading.ctrm.trade.TradeStatus;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class SimulationRequest {



     // 🔹 SINGLE_TRADE
    private String tradeId;

    // 🔹 FILTERED_TRADES
    private String desk;
    private TradeStatus status;
    private String instrument;
    private String counterparty;

    // 🔹 SAMPLE_TRADES
    private Integer sampleSize;

    private SimulationScope scope;

    // Rules to be applied in simulation
    @NotEmpty(message = "At least one ruleId must be provided")
    private List<Long> ruleIds;

    // Lifecycle context (REQUIRED for simulation)
    @NotNull(message = "fromStatus is mandatory")
    private TradeStatus fromStatus;

    @NotNull(message = "eventType is mandatory")
    private String eventType;   // BOOK, AMEND, CANCEL, APPROVE

    // Optional overrides (what-if simulation)
    private Map<String, Object> overrideAttributes;

    // -------- Getters & Setters --------

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public List<Long> getRuleIds() {
        return ruleIds;
    }

    public void setRuleIds(List<Long> ruleIds) {
        this.ruleIds = ruleIds;
    }

    public SimulationScope getScope() {
        return scope;
    }

    public void setScope(SimulationScope scope) {
        this.scope = scope;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }   

    public String getCounterparty() {
        return counterparty;
    }

    public void setCounterparty(String counterparty) {
        this.counterparty = counterparty;
    }   

    public Integer getSampleSize() {
        return sampleSize;
    }   

    public void setSampleSize(Integer sampleSize) {
        this.sampleSize = sampleSize;
    }

    public TradeStatus getStatus() {
        return status;
    }

    public TradeStatus getFromStatus() {
        return fromStatus;
    }

    public void setFromStatus(TradeStatus fromStatus) {
        this.fromStatus = fromStatus;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getDesk() {
        return desk;
    }

    public void setDesk(String desk) {
        this.desk = desk;
    }

    public Map<String, Object> getOverrideAttributes() {
        return overrideAttributes;
    }

    public void setOverrideAttributes(Map<String, Object> overrideAttributes) {
        this.overrideAttributes = overrideAttributes;
    }
}
