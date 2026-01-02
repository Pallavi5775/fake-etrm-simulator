package com.trading.ctrm.rules;

import org.springframework.stereotype.Component;

@Component
public class FieldResolver {

    public Object resolve(String fieldCode, TradeContext trade) {
        // Support both UPPER_CASE and camelCase
        String normalized = fieldCode.toLowerCase();
        
        return switch (normalized) {
            case "quantity" -> trade.quantity();
            case "counterparty" -> trade.counterparty();
            case "portfolio" -> trade.portfolio();
            case "instrumenttype", "instrument_type" -> trade.instrumentType();
            case "tradeid", "trade_id" -> trade.tradeId();
            default -> throw new IllegalArgumentException(
                "Unsupported field: " + fieldCode
            );
        };
    }
}
