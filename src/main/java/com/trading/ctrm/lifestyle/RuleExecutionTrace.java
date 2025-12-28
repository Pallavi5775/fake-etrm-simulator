package com.trading.ctrm.lifestyle;

import java.util.List;

public class RuleExecutionTrace {

    private Long ruleId;
    private boolean conditionMatched;
    private List<String> actionsExecuted;
    private String result;

    // ---- getters & setters ----

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public boolean isConditionMatched() {
        return conditionMatched;
    }

    public void setConditionMatched(boolean conditionMatched) {
        this.conditionMatched = conditionMatched;
    }

    public List<String> getActionsExecuted() {
        return actionsExecuted;
    }

    public void setActionsExecuted(List<String> actionsExecuted) {
        this.actionsExecuted = actionsExecuted;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
