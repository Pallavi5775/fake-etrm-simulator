package com.trading.ctrm.rules;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class ApprovalRuleEngine {

    private final ApprovalRuleRepository ruleRepo;
    private final FieldResolver fieldResolver;
    private final OperatorEvaluator operatorEvaluator;

    public ApprovalRuleEngine(
        ApprovalRuleRepository ruleRepo,
        FieldResolver fieldResolver,
        OperatorEvaluator operatorEvaluator
    ) {
        this.ruleRepo = ruleRepo;
        this.fieldResolver = fieldResolver;
        this.operatorEvaluator = operatorEvaluator;
    }

    public Optional<ApprovalRule> evaluate(
            TradeContext trade,
            String triggerEvent
    ) {

        List<ApprovalRule> rules =
            ruleRepo.findActiveRules(triggerEvent);

        System.out.println("📋 Found " + rules.size() + " active rules for event: " + triggerEvent);

        for (ApprovalRule rule : rules) {
            System.out.println("   Checking rule: " + rule.getRuleName() + " (ID: " + rule.getRuleId() + ")");
            if (matches(rule, trade)) {
                System.out.println("   ✅ Rule matched!");
                return Optional.of(rule);
            }
            System.out.println("   ❌ Rule did not match");
        }
        return Optional.empty();
    }

    private boolean matches(ApprovalRule rule, TradeContext trade) {

        for (ApprovalRuleCondition c : rule.getConditions()) {

            Object tradeValue =
                fieldResolver.resolve(c.getFieldCode(), trade);

            boolean result =
                operatorEvaluator.evaluate(
                    c.getOperator(),
                    tradeValue,
                    c.getValue1()
                );

            System.out.println("      Condition: " + c.getFieldCode() + " " + c.getOperator() + " " + c.getValue1());
            System.out.println("      Trade value: " + tradeValue + " -> " + (result ? "PASS" : "FAIL"));

            if (!result) {
                return false; // short-circuit
            }
        }
        return true;
    }
}

