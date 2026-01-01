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
@Table(name = "approval_routing")
public class ApprovalRouting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "routing_id")
    private Long routingId;

    @Column(name = "approval_role")
    private String approvalRole;
    
    @Column(name = "approval_level")
    private Integer approvalLevel;

    @ManyToOne
    @JoinColumn(name = "rule_id")
    private ApprovalRule rule;

    public Long getRoutingId() {
        return routingId;
    }

    public void setRoutingId(Long routingId) {
        this.routingId = routingId;
    }

    public ApprovalRule getRule() {
        return rule;
    }

    public void setRule(ApprovalRule rule) {
        this.rule = rule;
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

    public ApprovalRouting cloneFor(ApprovalRule newRule) {
        ApprovalRouting clone = new ApprovalRouting();
        clone.setApprovalRole(this.approvalRole);
        clone.setApprovalLevel(this.approvalLevel);
        clone.setRule(newRule);
        return clone;
    }
}

