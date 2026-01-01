package com.trading.ctrm.rules;

import org.springframework.stereotype.Component;

@Component
public class FieldResolver {

    public Object resolve(String fieldCode, TradeContext trade) {
        // Support both UPPER_CASE and camelCase
        String normalized = fieldCode.toLowerCase();
        
        return switch (normalized) {
            case "quantity" -> trade.getQuantity();
            case "counterparty" -> trade.getCounterparty();
            case "price" -> trade.getPrice();
            case "portfolio" -> trade.getPortfolio();
            case "instrumenttype", "instrument_type" -> trade.getInstrumentType();
            case "tradeid", "trade_id" -> trade.getTradeId();
            default -> throw new IllegalArgumentException(
                "Unsupported field: " + fieldCode
            );
        };
    }
}
