package com.trading.ctrm.trade;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.EnumType;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
@Entity
@Table(name = "trade_events")
public class TradeEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trade_id", nullable = false)
    private Trade trade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TradeEventType eventType;

    @Column(nullable = false)
    private String triggeredBy;

    @Column(nullable = false)
    private String source;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected TradeEvent() {
        // JPA only
    }

    private TradeEvent(
            Trade trade,
            TradeEventType eventType,
            String triggeredBy,
            String source,
            LocalDateTime createdAt) {

        this.trade = trade;
        this.eventType = eventType;
        this.triggeredBy = triggeredBy;
        this.source = source;
        this.createdAt = createdAt;
    }

    public static @NonNull TradeEvent of(
            Trade trade,
            TradeEventType eventType,
            String triggeredBy,
            String source) {

        if (trade == null) {
            throw new IllegalArgumentException("Trade must not be null");
        }
        if (eventType == null) {
            throw new IllegalArgumentException("EventType must not be null");
        }

        return new TradeEvent(
                trade,
                eventType,
                triggeredBy,
                source,
                LocalDateTime.now()
        );
    }


 

    // ---- getters ----

    public Long getId() {
        return id;
    }

    public Trade getTrade() {
        return trade;
    }

    public TradeEventType getEventType() {
        return eventType;
    }

    public String getTriggeredBy() {
        return triggeredBy;
    }

    public String getSource() {
        return source;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
