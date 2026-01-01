package com.trading.ctrm.rules.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.trading.ctrm.rules.ApprovalRule;

public class ApprovalRuleDto {
    private Long ruleId;
    private String ruleName;
    private String triggerEvent;
    private Integer priority;
    private Boolean active;
    private String status;
    private Integer version;
    private Long parentRuleId;
    private List<ApprovalRuleConditionDto> conditions;
    private List<ApprovalRoutingDto> routing;

    public static ApprovalRuleDto from(ApprovalRule entity) {
        ApprovalRuleDto dto = new ApprovalRuleDto();
        dto.setRuleId(entity.getRuleId());
        dto.setRuleName(entity.getRuleName());
        dto.setTriggerEvent(entity.getTriggerEvent());
        dto.setPriority(entity.getPriority());
        dto.setActive(entity.getActive());
        dto.setStatus(entity.getStatus());
        dto.setVersion(entity.getVersion());
        dto.setParentRuleId(entity.getParentRuleId());
        
        if (entity.getConditions() != null) {
            dto.setConditions(entity.getConditions().stream()
                .map(ApprovalRuleConditionDto::from)
                .collect(Collectors.toList()));
        }
        
        if (entity.getRouting() != null) {
            dto.setRouting(entity.getRouting().stream()
                .map(ApprovalRoutingDto::from)
                .collect(Collectors.toList()));
        }
        
        return dto;
    }

    // Getters and Setters
    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getTriggerEvent() {
        return triggerEvent;
    }

    public void setTriggerEvent(String triggerEvent) {
        this.triggerEvent = triggerEvent;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Long getParentRuleId() {
        return parentRuleId;
    }

    public void setParentRuleId(Long parentRuleId) {
        this.parentRuleId = parentRuleId;
    }

    public List<ApprovalRuleConditionDto> getConditions() {
        return conditions;
    }

    public void setConditions(List<ApprovalRuleConditionDto> conditions) {
        this.conditions = conditions;
    }

    public List<ApprovalRoutingDto> getRouting() {
        return routing;
    }

    public void setRouting(List<ApprovalRoutingDto> routing) {
        this.routing = routing;
    }
}
