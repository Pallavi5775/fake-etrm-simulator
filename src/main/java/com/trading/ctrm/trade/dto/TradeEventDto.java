package com.trading.ctrm.trade.dto;

import java.time.LocalDateTime;
import com.trading.ctrm.trade.TradeEventType;

public class TradeEventDto {
    private Long id;
    private TradeEventType eventType;
    private String triggeredBy;
    private String source;
    private LocalDateTime createdAt;

    public TradeEventDto() {}

    public TradeEventDto(Long id, TradeEventType eventType, String triggeredBy, String source, LocalDateTime createdAt) {
        this.id = id;
        this.eventType = eventType;
        this.triggeredBy = triggeredBy;
        this.source = source;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TradeEventType getEventType() { return eventType; }
    public void setEventType(TradeEventType eventType) { this.eventType = eventType; }

    public String getTriggeredBy() { return triggeredBy; }
    public void setTriggeredBy(String triggeredBy) { this.triggeredBy = triggeredBy; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
