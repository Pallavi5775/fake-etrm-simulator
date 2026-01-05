package com.trading.ctrm.trade;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TradeStatusUtil {
    public static List<TradeStatus> getValuableStatuses() {
        return Arrays.stream(TradeStatus.values())
                .filter(status -> status != TradeStatus.SETTLED
                        && status != TradeStatus.REJECTED
                        && status != TradeStatus.CANCELLED)
                .collect(Collectors.toList());
    }
}
