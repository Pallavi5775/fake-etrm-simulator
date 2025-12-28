package com.trading.ctrm.lifestyle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.trading.ctrm.trade.Trade;
import com.trading.ctrm.trade.TradeStatus;



public class ExecutionContext {

    private Trade trade;
    private Map<String, Object> attributes = new HashMap<>();
    private boolean dryRun = true;

    private TradeStatus originalStatus;

    private ExecutionMode mode; // SIMULATOR, RUNTIME, REPLAY

    private List<String> executedActions = new ArrayList<>();

    // -------- Getters & Setters --------


    public TradeStatus getOriginalStatus() {
        return originalStatus;
    }

    public void setOriginalStatus(TradeStatus originalStatus) {
        this.originalStatus = originalStatus;
    }

    public ExecutionMode getMode() {
        return mode;
    }

    public void setMode(ExecutionMode mode) {
        this.mode = mode;
    }

    public void markAction(String action) {
        executedActions.add(action);
    }

    public List<String> getExecutedActions() {
        return executedActions;
    }

    public Trade getTrade() {
        return trade;
    }

    public void setTrade(Trade trade) {
        this.trade = trade;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public boolean isDryRun() {
        return dryRun;
    }

    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }
}
