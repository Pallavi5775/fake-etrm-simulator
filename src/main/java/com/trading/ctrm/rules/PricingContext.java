package com.trading.ctrm.rules;

import com.trading.ctrm.trade.Trade;

public class PricingContext {

    private final String pricingModel;   // DCF, Black76
    private final String dayCount;
    private final String compounding;
    private final String settlementType;

    public PricingContext(
            String pricingModel,
            String dayCount,
            String compounding,
            String settlementType
    ) {
        this.pricingModel = pricingModel;
        this.dayCount = dayCount;
        this.compounding = compounding;
        this.settlementType = settlementType;
    }

    public String pricingModel() { return pricingModel; }
    public String dayCount() { return dayCount; }
    public String compounding() { return compounding; }
    public String settlementType() { return settlementType; }

    public static PricingContext of(
            String pricingModel,
            String dayCount,
            String compounding,
            String settlementType
    ) {
        return new PricingContext(
                pricingModel,
                dayCount,
                compounding,
                settlementType
        );
    }

    /**
     * Factory method to create PricingContext from a Trade entity with default pricing settings
     */
    public static PricingContext fromTrade(Trade trade) {
        String instrumentType = trade.getInstrument().getInstrumentType().name();
        String pricingModel = instrumentType.contains("OPTION") ? "Black76" : "DCF";
        String settlementType = instrumentType.contains("PHYSICAL") ? "PHYSICAL" : "CASH";
        
        return new PricingContext(
                pricingModel,
                "ACT_365",
                "CONTINUOUS",
                settlementType
        );
    }

    /**
     * Factory method with configurable parameters from UI
     */
    public static PricingContext fromTrade(
            Trade trade,
            String pricingModel,
            String dayCount,
            String compounding,
            String settlementType
    ) {
        String instrumentType = trade.getInstrument().getInstrumentType().name();
        String defaultModel = instrumentType.contains("OPTION") ? "Black76" : "DCF";
        String defaultSettlement = instrumentType.contains("PHYSICAL") ? "PHYSICAL" : "CASH";
        
        return new PricingContext(
                pricingModel != null ? pricingModel : defaultModel,
                dayCount != null ? dayCount : "ACT_365",
                compounding != null ? compounding : "CONTINUOUS",
                settlementType != null ? settlementType : defaultSettlement
        );
    }
}