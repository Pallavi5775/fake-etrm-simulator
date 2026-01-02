package com.trading.ctrm.rules;
import java.time.Instant;
import com.trading.ctrm.trade.Trade;

public class AuditContext {

    private final String user;
    private final String legalEntity;
    private final String sourceSystem;
    private final Instant runTimestamp;

    public AuditContext(
            String user,
            String legalEntity,
            String sourceSystem
    ) {
        this.user = user;
        this.legalEntity = legalEntity;
        this.sourceSystem = sourceSystem;
        this.runTimestamp = Instant.now();
    }

    public String user() { return user; }
    public String legalEntity() { return legalEntity; }
    public String sourceSystem() { return sourceSystem; }
    public Instant runTimestamp() { return runTimestamp; }

    public static AuditContext of(
            String user,
            String legalEntity,
            String sourceSystem
    ) {
        return new AuditContext(
                user,
                legalEntity,
                sourceSystem
        );
    }

    /**
     * Factory method to create AuditContext from a Trade entity with default audit settings
     */
    public static AuditContext fromTrade(Trade trade) {
        return new AuditContext(
                "SYSTEM_USER",
                "LE_US",
                "CTRMSIM"
        );
    }

    /**
     * Factory method with configurable parameters from UI
     */
    public static AuditContext fromTrade(
            Trade trade,
            String user,
            String legalEntity,
            String sourceSystem
    ) {
        return new AuditContext(
                user != null ? user : "SYSTEM_USER",
                legalEntity != null ? legalEntity : "LE_US",
                sourceSystem != null ? sourceSystem : "CTRMSIM"
        );
    }
}