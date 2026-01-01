package com.trading.ctrm.auth;

public enum UserRole {
    RISK("Risk Manager"),
    SENIOR_TRADER("Senior Trader"),
    HEAD_TRADER("Head Trader"),
    COMPLIANCE("Compliance"),
    CFO("CFO"),
    ADMIN("Administrator");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
