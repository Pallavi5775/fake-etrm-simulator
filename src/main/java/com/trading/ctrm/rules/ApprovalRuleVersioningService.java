package com.trading.ctrm.rules;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class ApprovalRuleVersioningService {

    private final ApprovalRuleRepository repo;

    public ApprovalRuleVersioningService(
        ApprovalRuleRepository repo
    ) {
        this.repo = repo;
    }


    @Transactional
public void activateRule(Long ruleId) {
    ApprovalRule rule = repo.findById(ruleId)
        .orElseThrow();

    Long parent =
        rule.getParentRuleId() != null
            ? rule.getParentRuleId()
            : rule.getRuleId();

    // Deactivate all sibling versions
    repo.deactivateAllVersions(parent);

    rule.setActive(true);
    rule.setStatus("ACTIVE");

    repo.save(rule);
}


    public ApprovalRule createNewVersion(Long ruleId) {

        ApprovalRule old = repo.findById(ruleId)
            .orElseThrow();

        ApprovalRule copy = new ApprovalRule();

        copy.setRuleName(old.getRuleName());
        copy.setTriggerEvent(old.getTriggerEvent());
        copy.setPriority(old.getPriority());
        copy.setActive(false);
        copy.setStatus("DRAFT");
        copy.setVersion(old.getVersion() + 1);
        copy.setParentRuleId(
            old.getParentRuleId() != null
                ? old.getParentRuleId()
                : old.getRuleId()
        );

        // Deep copy conditions
        copy.setConditions(
            old.getConditions().stream()
                .map(c -> c.cloneFor(copy))
                .toList()
        );

        // Deep copy routing
        copy.setRouting(
            old.getRouting().stream()
                .map(r -> r.cloneFor(copy))
                .toList()
        );

        return repo.save(copy);
    }
}
