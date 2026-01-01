package com.trading.ctrm.rules;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "approval_rule_condition")
public class ApprovalRuleCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "condition_id")
    private Long conditionId;

    @Column(name = "field_code")
    private String fieldCode;
    
    @Column(name = "operator")
    private String operator;
    
    @Column(name = "value1")
    private String value1;

    @ManyToOne
    @JoinColumn(name = "rule_id")
    private ApprovalRule rule;

    public Long getConditionId() {
        return conditionId;
    }

    public void setConditionId(Long conditionId) {
        this.conditionId = conditionId;
    }

    public ApprovalRule getRule() {
        return rule;
    }

    public void setRule(ApprovalRule rule) {
        this.rule = rule;
    }

     public String getFieldCode() {
        return fieldCode;
    }

    public void setFieldCode(String fieldCode) {
        this.fieldCode = fieldCode;
    }

     public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

     public String getValue1() {
        return value1;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }

    public ApprovalRuleCondition cloneFor(ApprovalRule newRule) {
        ApprovalRuleCondition clone = new ApprovalRuleCondition();
        clone.setFieldCode(this.fieldCode);
        clone.setOperator(this.operator);
        clone.setValue1(this.value1);
        clone.setRule(newRule);
        return clone;
    }
}

