package com.trading.ctrm.trade;

public enum TradeEventType {

    CREATED,     // trade booked
    AMENDED,     // trade amended
    PRICED,      // MTM / pricing completed
    DELIVERED,   // physical delivery done
    INVOICED,    // invoice generated
    SETTLED,     // cash settled
    CANCELLED,   // trade cancelled
    REJECTED,
    APPROVED
}

