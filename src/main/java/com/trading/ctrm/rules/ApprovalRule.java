package com.trading.ctrm.rules;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "approval_rule", schema = "ctrm")
public class ApprovalRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rule_id")
    private Long ruleId;

    @Column(name = "rule_name")
    private String ruleName;
    
    @Column(name = "trigger_event")
    private String triggerEvent;
    
    @Column(name = "priority")
    private Integer priority;
    
    @Column(name = "active")
    private Boolean active;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "version")
    private Integer version;
    
    @Column(name = "parent_rule_id")
    private Long parentRuleId;

    @OneToMany(mappedBy = "rule", cascade = CascadeType.ALL)
    private List<ApprovalRuleCondition> conditions;

    @OneToMany(mappedBy = "rule", cascade = CascadeType.ALL)
    private List<ApprovalRouting> routing;

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

    public List<ApprovalRuleCondition> getConditions() {
        return conditions;
    }

    public void setConditions(List<ApprovalRuleCondition> conditions) {
        this.conditions = conditions;
    }

    public List<ApprovalRouting> getRouting() {
        return routing;
    }

    public void setRouting(List<ApprovalRouting> routing) {
        this.routing = routing;
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
}

