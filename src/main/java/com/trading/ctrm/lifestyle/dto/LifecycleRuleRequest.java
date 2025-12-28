package com.trading.ctrm.lifestyle.dto;

import com.trading.ctrm.trade.TradeEventType;
import com.trading.ctrm.trade.TradeStatus;

import java.time.LocalDate;

public class LifecycleRuleRequest {

    private TradeStatus fromStatus;
    private TradeEventType eventType;
    private TradeStatus toStatus;
    private int maxOccurrence;
    private boolean enabled;
    private String desk;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private String name;

    // 

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    

    // getters
    public TradeStatus getFromStatus() {
        return fromStatus;
    }

    public TradeEventType getEventType() {
        return eventType;
    }

    public TradeStatus getToStatus() {
        return toStatus;
    }

    public int getMaxOccurrence() {
        return maxOccurrence;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getDesk() {
        return desk;
    }

    public LocalDate getEffectiveFrom() {
        return effectiveFrom;
    }

    public LocalDate getEffectiveTo() {
        return effectiveTo;
    }
}
