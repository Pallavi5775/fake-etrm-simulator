package com.trading.ctrm.rules.dto;

import com.trading.ctrm.rules.ApprovalRouting;

public class ApprovalRoutingDto {
    private Long routingId;
    private String approvalRole;
    private Integer approvalLevel;

    public static ApprovalRoutingDto from(ApprovalRouting entity) {
        ApprovalRoutingDto dto = new ApprovalRoutingDto();
        dto.setRoutingId(entity.getRoutingId());
        dto.setApprovalRole(entity.getApprovalRole());
        dto.setApprovalLevel(entity.getApprovalLevel());
        return dto;
    }

    // Getters and Setters
    public Long getRoutingId() {
        return routingId;
    }

    public void setRoutingId(Long routingId) {
        this.routingId = routingId;
    }

    public String getApprovalRole() {
        return approvalRole;
    }

    public void setApprovalRole(String approvalRole) {
        this.approvalRole = approvalRole;
    }

    public Integer getApprovalLevel() {
        return approvalLevel;
    }

    public void setApprovalLevel(Integer approvalLevel) {
        this.approvalLevel = approvalLevel;
    }
}
