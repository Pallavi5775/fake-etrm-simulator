package com.trading.ctrm.lifestyle;

import com.trading.ctrm.common.BusinessException;
import com.trading.ctrm.trade.TradeEventType;
import com.trading.ctrm.trade.TradeStatus;

public class TradeLifecycleValidator {

    public static void validate(
            TradeStatus currentStatus,
            TradeEventType eventType) {

        switch (currentStatus) {

            case CREATED:
                require(
                    eventType == TradeEventType.PRICED
                    || eventType == TradeEventType.CANCELLED,
                    "From CREATED, only PRICED or CANCELLED is allowed"
                );
                break;

            case PRICED:
                require(
                    eventType == TradeEventType.AMENDED
                    || eventType == TradeEventType.DELIVERED,
                    "From PRICED, only AMENDED or DELIVERED is allowed"
                );
                break;

            case DELIVERED:
                require(
                    eventType == TradeEventType.INVOICED,
                    "From DELIVERED, only INVOICED is allowed"
                );
                break;

            case INVOICED:
                require(
                    eventType == TradeEventType.SETTLED,
                    "From INVOICED, only SETTLED is allowed"
                );
                break;

            case SETTLED:
                throw new BusinessException(
                    "Settled trade cannot be modified"
                );

            case CANCELLED:
                throw new BusinessException(
                    "Cancelled trade cannot be modified"
                );

            default:
                throw new BusinessException(
                    "Unsupported trade status: " + currentStatus
                );
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new BusinessException(message);
        }
    }
}
