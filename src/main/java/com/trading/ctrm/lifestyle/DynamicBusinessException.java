package com.trading.ctrm.lifestyle;

public class DynamicBusinessException extends RuntimeException {
    private final RejectionReason reason;

    public DynamicBusinessException(RejectionReason reason, String message) {
        super(message);
        this.reason = reason;
    }

    public RejectionReason getReason() {
        return reason;
    }
}

