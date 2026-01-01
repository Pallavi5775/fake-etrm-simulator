package com.trading.ctrm.rules;

import org.springframework.stereotype.Service;

@Service
public class ApprovalExecutionService {

    private final ApprovalTaskRepository approvalTaskRepository;

    public ApprovalExecutionService(ApprovalTaskRepository approvalTaskRepository) {
        this.approvalTaskRepository = approvalTaskRepository;
    }

    public void createPendingApproval(
            TradeContext tradeContext,
            ApprovalRule rule,
            String triggerEvent
    ) {
        // Create approval task for the trade
        ApprovalTask task = new ApprovalTask();
        task.setTradeId(tradeContext.getTradeId());
        task.setRuleId(rule.getRuleId());
        task.setTriggerEvent(triggerEvent);
        task.setStatus("PENDING");
        
        // Get first level approver from routing
        rule.getRouting().stream()
            .filter(r -> r.getApprovalLevel() == 1)
            .findFirst()
            .ifPresent(routing -> {
                task.setCurrentApprovalRole(routing.getApprovalRole());
                task.setCurrentApprovalLevel(routing.getApprovalLevel());
            });
        
        approvalTaskRepository.save(task);
    }
}
