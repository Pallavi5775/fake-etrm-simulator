package com.trading.ctrm.lifestyle;

import com.trading.ctrm.trade.Trade;
import com.trading.ctrm.trade.TradeEventType;
import com.trading.ctrm.trade.TradeStatus;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "lifecycle_rules")
public class LifecycleRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status", nullable = false)
    private TradeStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private TradeEventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false)
    private TradeStatus toStatus;

    @Enumerated(EnumType.STRING)
    private TradeStatus fromState;

    @Enumerated(EnumType.STRING)
    private TradeStatus toState;

    @Column(name = "max_occurrence", nullable = false)
    private int maxOccurrence;

    @Column(nullable = false)
    private String event;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name = "desk", nullable = false)
    private String desk;

    @Column(name = "effective_from")
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Column(name = "auto_approve", nullable = false)
    private Boolean autoApprove = Boolean.FALSE;

    @Column
    private String approvalRole; // e.g. "RISK_MANAGER"

    @Column
    private String name;

    @Version
    @Column(name = "version", nullable = false)
    private int version;
 // 🔑 NEW: runtime enablement (promotion flag)
    private boolean productionEnabled;
    protected LifecycleRule() {}

    /* -------------------------------------------------
       DOMAIN METHODS (UPDATED)
       ------------------------------------------------- */

    /** Human-readable name for UI / audit */
    public String getName() {
        return name;
    }

    /** Rule applicability check using execution context */
    public boolean evaluate(ExecutionContext ctx) {

        if (!enabled) return false;

        Trade trade = ctx.getTrade();
        if (trade == null) return false;

        // status match
        if (trade.getStatus() != fromStatus) return false;

        // event match (must be supplied in context attributes)
        TradeEventType incomingEvent =
                (TradeEventType) ctx.getAttributes().get("eventType");
        if (incomingEvent == null || incomingEvent != eventType) return false;

        // desk / portfolio match
        if (!desk.equalsIgnoreCase(trade.getPortfolio())) return false;

        LocalDate asOf = LocalDate.now();

        if (effectiveFrom != null && asOf.isBefore(effectiveFrom)) return false;
        if (effectiveTo != null && asOf.isAfter(effectiveTo)) return false;

        return true;
    }

    /** Dry-run lifecycle action (NO DB mutation) */
    public List<String> simulate(ExecutionContext ctx) {

        if (!ctx.isDryRun()) {
            throw new IllegalStateException(
                    "simulate() called with dryRun=false for rule " + getName()
            );
        }

        return List.of(
            "Transition trade status from " + fromStatus + " → " + toStatus
        );
    }

    public void toggleEnabled() {
        this.enabled = !this.enabled;
    }


    public void execute(ExecutionContext ctx) {

        Trade trade = ctx.getTrade();

        // 1️⃣ Validate state
        if (trade.getStatus() != fromState) {
            return;
        }

        // 2️⃣ Simulator mode
        if (ctx.getMode() == ExecutionMode.SIMULATOR) {
            ctx.markAction(
                "Would transition trade from " +
                fromState + " to " + toState
            );
            return;
        }

        // 3️⃣ Runtime mode
        if (ctx.getMode() == ExecutionMode.RUNTIME) {
            trade.setStatus(toState);
            ctx.markAction(
                "Transitioned trade from " +
                fromState + " to " + toState
            );
        }

        // 4️⃣ Replay mode
        if (ctx.getMode() == ExecutionMode.REPLAY) {
            ctx.markAction(
                "Replayed transition from " +
                fromState + " to " + toState
            );
        }
    }

    public boolean isProductionEnabled() {
        return productionEnabled;
    }

    public void setProductionEnabled(boolean productionEnabled) {
        this.productionEnabled = productionEnabled;
    }

    /* -------------------------------------------------
       Getters (immutable-style)
       ------------------------------------------------- */


       public String getApprovalRole() {
        return approvalRole;
       }

       public void setApprovalRole(String approvalRole) {
        this.approvalRole = approvalRole;
       }


       public String getEvent() {
        return event;
       }

       public void setEvent(String event) {
        this.event = event;
       }


       public void setAutoApprove(Boolean autoApprove) {
        this.autoApprove = autoApprove;
       }

       public void setName(String name) {
        this.name = name;
       }

       public void setId(Long id) {
        this.id = id;
       }

    public Long getId() {
        return id;
    }

    public boolean isAutoApprove() {
        return autoApprove;
    }

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

    public int getVersion() {
        return version;
    }


    public void setFromStatus(TradeStatus fromStatus) {
    this.fromStatus = fromStatus;
}

public void setEventType(TradeEventType eventType) {
    this.eventType = eventType;
}

public void setToStatus(TradeStatus toStatus) {
    this.toStatus = toStatus;
}

public void setMaxOccurrence(int maxOccurrence) {
    this.maxOccurrence = maxOccurrence;
}

public void setEnabled(boolean enabled) {
    this.enabled = enabled;
}

public void setDesk(String desk) {
    this.desk = desk;
}

public void setEffectiveFrom(LocalDate effectiveFrom) {
    this.effectiveFrom = effectiveFrom;
}

public void setEffectiveTo(LocalDate effectiveTo) {
    this.effectiveTo = effectiveTo;
}

}
