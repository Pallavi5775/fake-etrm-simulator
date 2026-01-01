package com.trading.ctrm.rules.dto;

import com.trading.ctrm.rules.ApprovalRuleCondition;

public class ApprovalRuleConditionDto {
    private Long conditionId;
    private String fieldCode;
    private String operator;
    private String value1;

    public static ApprovalRuleConditionDto from(ApprovalRuleCondition entity) {
        ApprovalRuleConditionDto dto = new ApprovalRuleConditionDto();
        dto.setConditionId(entity.getConditionId());
        dto.setFieldCode(entity.getFieldCode());
        dto.setOperator(entity.getOperator());
        dto.setValue1(entity.getValue1());
        return dto;
    }

    // Getters and Setters
    public Long getConditionId() {
        return conditionId;
    }

    public void setConditionId(Long conditionId) {
        this.conditionId = conditionId;
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
}
