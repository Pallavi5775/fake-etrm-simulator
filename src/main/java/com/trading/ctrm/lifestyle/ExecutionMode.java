package com.trading.ctrm.lifestyle;

public enum ExecutionMode {

    /**
     * Dry-run mode
     * - No DB writes
     * - No external events
     * - Used by Rule Simulator
     */
    SIMULATOR,

    /**
     * Real execution
     * - DB writes enabled
     * - Events published
     * - Transactions & locks applied
     */
    RUNTIME,

    /**
     * Historical replay
     * - No DB writes
     * - Deterministic re-execution
     * - Used for audit & debugging
     */
    REPLAY
}
