package com.trading.ctrm.rules;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ApprovalRoutingService {

    private final ApprovalRuleRepository approvalRuleRepository;

    public ApprovalRoutingService(ApprovalRuleRepository approvalRuleRepository) {
        this.approvalRuleRepository = approvalRuleRepository;
    }

    /**
     * Get the next approval routing level for a trade
     * @param matchedRuleId the rule ID matched to this trade
     * @param currentLevel the current approval level (e.g., 1, 2, 3)
     * @return the next ApprovalRouting if exists, empty otherwise
     */
    public Optional<ApprovalRouting> getNextLevel(Long matchedRuleId, Integer currentLevel) {
        if (matchedRuleId == null || currentLevel == null) {
            return Optional.empty();
        }

        ApprovalRule rule = approvalRuleRepository.findById(matchedRuleId)
                .orElse(null);
        
        if (rule == null || rule.getRouting() == null) {
            return Optional.empty();
        }

        int nextLevel = currentLevel + 1;
        
        return rule.getRouting().stream()
                .filter(r -> r.getApprovalLevel() != null && r.getApprovalLevel() == nextLevel)
                .findFirst();
    }

    /**
     * Check if there are more approval levels after the current one
     */
    public boolean hasMoreLevels(Long matchedRuleId, Integer currentLevel) {
        return getNextLevel(matchedRuleId, currentLevel).isPresent();
    }
}
