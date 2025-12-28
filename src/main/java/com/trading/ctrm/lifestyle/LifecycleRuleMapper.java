package com.trading.ctrm.lifestyle;
import com.trading.ctrm.lifestyle.dto.LifecycleRuleResponse;

public class LifecycleRuleMapper {

    public static LifecycleRuleResponse toResponse(LifecycleRule rule) {

        LifecycleRuleResponse res = new LifecycleRuleResponse();

        res.setId(rule.getId());
        res.setName(rule.getName());

        res.setFromStatus(rule.getFromStatus());
        res.setEventType(rule.getEventType());
        res.setToStatus(rule.getToStatus());

        res.setDesk(rule.getDesk());

        res.setMaxOccurrence(rule.getMaxOccurrence());

        res.setEnabled(rule.isEnabled());
        res.setProductionEnabled(rule.isProductionEnabled());

        res.setEffectiveFrom(rule.getEffectiveFrom());
        res.setEffectiveTo(rule.getEffectiveTo());

        return res;
    }
}

