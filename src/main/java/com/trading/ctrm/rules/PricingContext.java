package com.trading.ctrm.rules;

import com.trading.ctrm.trade.Trade;


import java.util.List;

public class PricingContext {

        private final String pricingModel;   // DCF, Black76
        private final String dayCount;
        private final String compounding;
        private final String settlementType;

        // Added for pricing engine context
        private final java.math.BigDecimal forwardPrice;
        private final java.math.BigDecimal volatility;
        private final java.math.BigDecimal discountRate;
        private final Double yearsToExpiry;
        // Added for DCF pricing
        private final List<java.math.BigDecimal> cashFlows;
        private final List<Double> cashFlowTimes;


        public PricingContext(
                        String pricingModel,
                        String dayCount,
                        String compounding,
                        String settlementType
        ) {
                this(pricingModel, dayCount, compounding, settlementType, null, null, null, null, null, null);
        }

        public PricingContext(
                        String pricingModel,
                        String dayCount,
                        String compounding,
                        String settlementType,
                        java.math.BigDecimal forwardPrice,
                        java.math.BigDecimal volatility,
                        java.math.BigDecimal discountRate,
                        Double yearsToExpiry,
                        List<java.math.BigDecimal> cashFlows,
                        List<Double> cashFlowTimes
        ) {
                this.pricingModel = pricingModel;
                this.dayCount = dayCount;
                this.compounding = compounding;
                this.settlementType = settlementType;
                this.forwardPrice = forwardPrice;
                this.volatility = volatility;
                this.discountRate = discountRate;
                this.yearsToExpiry = yearsToExpiry;
                this.cashFlows = cashFlows;
                this.cashFlowTimes = cashFlowTimes;
        }

        public String pricingModel() { return pricingModel; }
        public String dayCount() { return dayCount; }
        public String compounding() { return compounding; }
        public String settlementType() { return settlementType; }
        public java.math.BigDecimal forwardPrice() { return forwardPrice; }
        public java.math.BigDecimal volatility() { return volatility; }
        public java.math.BigDecimal discountRate() { return discountRate; }
        public Double yearsToExpiry() { return yearsToExpiry; }
        public List<java.math.BigDecimal> cashFlows() { return cashFlows; }
        public List<Double> cashFlowTimes() { return cashFlowTimes; }

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

    public static PricingContext of(
            String pricingModel,
            String dayCount,
            String compounding,
            String settlementType,
            java.math.BigDecimal forwardPrice,
            java.math.BigDecimal volatility,
            java.math.BigDecimal discountRate,
            Double yearsToExpiry,
            List<java.math.BigDecimal> cashFlows,
            List<Double> cashFlowTimes
    ) {
        return new PricingContext(
                pricingModel,
                dayCount,
                compounding,
                settlementType,
                forwardPrice,
                volatility,
                discountRate,
                yearsToExpiry,
                cashFlows,
                cashFlowTimes
        );
    }

    /**
     * Factory method to create PricingContext from a Trade entity with default pricing settings
     */
    public static PricingContext fromTrade(Trade trade) {
        String instrumentType = trade.getInstrument().getInstrumentType().name();
        String pricingModel = switch (instrumentType) {
            case "POWER_FORWARD" -> "POWER_FORWARD";
            case "OPTION" -> "Black76";
            case "RENEWABLE_PPA" -> "RENEWABLE_FORECAST";
            case "GAS_FORWARD", "COMMODITY_SWAP", "FREIGHT" -> "DCF";
            default -> "DCF";
        };
        String settlementType = instrumentType.contains("PHYSICAL") ? "PHYSICAL" : "CASH";

        // For options, calculate years to expiry
        Double yearsToExpiry = null;
        if ("OPTION".equals(instrumentType) && trade.getInstrument() instanceof com.trading.ctrm.instrument.CommodityOptionInstrument) {
            com.trading.ctrm.instrument.CommodityOptionInstrument optionInstrument =
                (com.trading.ctrm.instrument.CommodityOptionInstrument) trade.getInstrument();
            if (optionInstrument.getExpiryDate() != null) {
                java.time.LocalDate tradeDate = trade.getTradeDate() != null ? trade.getTradeDate() : java.time.LocalDate.now();
                yearsToExpiry = java.time.temporal.ChronoUnit.DAYS.between(tradeDate, optionInstrument.getExpiryDate()) / 365.0;
            }
        }

        return new PricingContext(
                pricingModel,
                "ACT_365",
                "CONTINUOUS",
                settlementType,
                null, // forwardPrice - will be looked up from market data
                null, // volatility - will be looked up from market data
                null, // discountRate - will be looked up from market data
                yearsToExpiry,
                null, // cashFlows
                null  // cashFlowTimes
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