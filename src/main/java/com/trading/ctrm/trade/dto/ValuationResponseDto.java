package com.trading.ctrm.trade.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ValuationResponseDto {
    
    private String tradeId;
    private LocalDate valuationDate;
    private Boolean isMultiLeg;
    private String strategyType;
    
    // Aggregate valuation (for all trades)
    private BigDecimal totalMtm;
    private BigDecimal totalPnl;
    private String pricingModel;
    
    // Single leg valuation
    private LegValuationDto singleLegValuation;
    
    // Multi-leg valuations
    private List<LegValuationDto> legValuations;
    
    public static class LegValuationDto {
        private Integer legNumber;
        private String instrumentCode;
        private String buySell;
        private BigDecimal quantity;
        private BigDecimal tradePrice;
        private BigDecimal marketPrice;
        private BigDecimal mtm;
        private BigDecimal pnl;
        private LocalDate deliveryDate;
        
        // Getters and Setters
        public Integer getLegNumber() {
            return legNumber;
        }
        
        public void setLegNumber(Integer legNumber) {
            this.legNumber = legNumber;
        }
        
        public String getInstrumentCode() {
            return instrumentCode;
        }
        
        public void setInstrumentCode(String instrumentCode) {
            this.instrumentCode = instrumentCode;
        }
        
        public String getBuySell() {
            return buySell;
        }
        
        public void setBuySell(String buySell) {
            this.buySell = buySell;
        }
        
        public BigDecimal getQuantity() {
            return quantity;
        }
        
        public void setQuantity(BigDecimal quantity) {
            this.quantity = quantity;
        }
        
        public BigDecimal getTradePrice() {
            return tradePrice;
        }
        
        public void setTradePrice(BigDecimal tradePrice) {
            this.tradePrice = tradePrice;
        }
        
        public BigDecimal getMarketPrice() {
            return marketPrice;
        }
        
        public void setMarketPrice(BigDecimal marketPrice) {
            this.marketPrice = marketPrice;
        }
        
        public BigDecimal getMtm() {
            return mtm;
        }
        
        public void setMtm(BigDecimal mtm) {
            this.mtm = mtm;
        }
        
        public BigDecimal getPnl() {
            return pnl;
        }
        
        public void setPnl(BigDecimal pnl) {
            this.pnl = pnl;
        }
        
        public LocalDate getDeliveryDate() {
            return deliveryDate;
        }
        
        public void setDeliveryDate(LocalDate deliveryDate) {
            this.deliveryDate = deliveryDate;
        }
    }
    
    // Getters and Setters
    public String getTradeId() {
        return tradeId;
    }
    
    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }
    
    public LocalDate getValuationDate() {
        return valuationDate;
    }
    
    public void setValuationDate(LocalDate valuationDate) {
        this.valuationDate = valuationDate;
    }
    
    public Boolean getIsMultiLeg() {
        return isMultiLeg;
    }
    
    public void setIsMultiLeg(Boolean isMultiLeg) {
        this.isMultiLeg = isMultiLeg;
    }
    
    public String getStrategyType() {
        return strategyType;
    }
    
    public void setStrategyType(String strategyType) {
        this.strategyType = strategyType;
    }
    
    public BigDecimal getTotalMtm() {
        return totalMtm;
    }
    
    public void setTotalMtm(BigDecimal totalMtm) {
        this.totalMtm = totalMtm;
    }
    
    public BigDecimal getTotalPnl() {
        return totalPnl;
    }
    
    public void setTotalPnl(BigDecimal totalPnl) {
        this.totalPnl = totalPnl;
    }
    
    public String getPricingModel() {
        return pricingModel;
    }
    
    public void setPricingModel(String pricingModel) {
        this.pricingModel = pricingModel;
    }
    
    public LegValuationDto getSingleLegValuation() {
        return singleLegValuation;
    }
    
    public void setSingleLegValuation(LegValuationDto singleLegValuation) {
        this.singleLegValuation = singleLegValuation;
    }
    
    public List<LegValuationDto> getLegValuations() {
        return legValuations;
    }
    
    public void setLegValuations(List<LegValuationDto> legValuations) {
        this.legValuations = legValuations;
    }
}
