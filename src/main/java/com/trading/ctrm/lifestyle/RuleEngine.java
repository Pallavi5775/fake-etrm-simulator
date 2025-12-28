package com.trading.ctrm.lifestyle;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class RuleEngine {

    public List<RuleExecutionTrace> simulate(
            List<LifecycleRule> rules,
            ExecutionContext ctx
    ) {
        List<RuleExecutionTrace> traces = new ArrayList<>();

        for (LifecycleRule rule : rules) {

            RuleExecutionTrace trace = new RuleExecutionTrace();
            trace.setRuleId(rule.getId());

            boolean matched = rule.evaluate(ctx);
            trace.setConditionMatched(matched);

            if (matched) {
                rule.execute(ctx); // simulator mode
                trace.setActionsExecuted(ctx.getExecutedActions());
                trace.setResult("SUCCESS");
            } else {
                trace.setResult("SKIPPED");
            }

            traces.add(trace);
        }
        return traces;
    }
}
