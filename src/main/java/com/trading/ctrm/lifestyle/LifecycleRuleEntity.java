package com.trading.ctrm.lifestyle;

import com.trading.ctrm.trade.TradeEventType;
import com.trading.ctrm.trade.TradeStatus;

import jakarta.persistence.*;

@Entity
@Table(name = "trade_lifecycle_rules")
public class LifecycleRuleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TradeStatus currentStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TradeEventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TradeStatus nextStatus;

    @Column(nullable = false)
    private int maxOccurrence;

    @Column(nullable = false)
    private boolean requiresApproval;

    @Column(nullable = false)
    private boolean auto;

    // =========================
    // JPA requires no-arg ctor
    // =========================
    protected LifecycleRuleEntity() {
    }

    // =========================
    // Convenience constructor
    // =========================
    public LifecycleRuleEntity(
            TradeStatus currentStatus,
            TradeEventType eventType,
            TradeStatus nextStatus,
            int maxOccurrence,
            boolean requiresApproval,
            boolean auto) {

        this.currentStatus = currentStatus;
        this.eventType = eventType;
        this.nextStatus = nextStatus;
        this.maxOccurrence = maxOccurrence;
        this.requiresApproval = requiresApproval;
        this.auto = auto;
    }

    // =========================
    // Getters
    // =========================
    public Long getId() {
        return id;
    }

    public TradeStatus getCurrentStatus() {
        return currentStatus;
    }

    public TradeEventType getEventType() {
        return eventType;
    }

    public TradeStatus getNextStatus() {
        return nextStatus;
    }

    public int getMaxOccurrence() {
        return maxOccurrence;
    }

    public boolean isRequiresApproval() {
        return requiresApproval;
    }

    public boolean isAuto() {
        return auto;
    }

    // =========================
    // Setters
    // =========================
    public void setCurrentStatus(TradeStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    public void setEventType(TradeEventType eventType) {
        this.eventType = eventType;
    }

    public void setNextStatus(TradeStatus nextStatus) {
        this.nextStatus = nextStatus;
    }

    public void setMaxOccurrence(int maxOccurrence) {
        this.maxOccurrence = maxOccurrence;
    }

    public void setRequiresApproval(boolean requiresApproval) {
        this.requiresApproval = requiresApproval;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }
}
