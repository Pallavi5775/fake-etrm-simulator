package com.trading.ctrm.lifestyle.dto;

import java.time.LocalDate;

import com.trading.ctrm.trade.TradeEventType;
import com.trading.ctrm.trade.TradeStatus;

public class LifecycleRuleResponse {

    private Long id;                     // System identity
    private String name;                 // Optional display label

    private TradeStatus fromStatus;
    private TradeEventType eventType;
    private TradeStatus toStatus;

    private String desk;

    private Integer maxOccurrence;

    private boolean enabled;
    private boolean productionEnabled;

    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

    // --- getters & setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TradeStatus getFromStatus() {
        return fromStatus;
    }

    public void setFromStatus(TradeStatus fromStatus) {
        this.fromStatus = fromStatus;
    }

    public TradeEventType getEventType() {
        return eventType;
    }

    public void setEventType(TradeEventType eventType) {
        this.eventType = eventType;
    }

    public TradeStatus getToStatus() {
        return toStatus;
    }

    public void setToStatus(TradeStatus toStatus) {
        this.toStatus = toStatus;
    }

    public String getDesk() {
        return desk;
    }

    public void setDesk(String desk) {
        this.desk = desk;
    }

    public Integer getMaxOccurrence() {
        return maxOccurrence;
    }

    public void setMaxOccurrence(Integer maxOccurrence) {
        this.maxOccurrence = maxOccurrence;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isProductionEnabled() {
        return productionEnabled;
    }

    public void setProductionEnabled(boolean productionEnabled) {
        this.productionEnabled = productionEnabled;
    }

    public LocalDate getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(LocalDate effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    public LocalDate getEffectiveTo() {
        return effectiveTo;
    }

    public void setEffectiveTo(LocalDate effectiveTo) {
        this.effectiveTo = effectiveTo;
    }
}
