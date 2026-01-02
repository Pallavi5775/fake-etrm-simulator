package com.trading.ctrm.trade.dto;

import java.time.LocalDate;

/**
 * DTO for UI to provide custom valuation context parameters
 */
public class ValuationConfigRequest {

    // Market Context
    private String marketDataSet;
    private LocalDate pricingDate;
    private String curveSet;
    private String fxScenario;
    private String volatilitySurface;

    // Pricing Context
    private String pricingModel;
    private String dayCount;
    private String compounding;
    private String settlementType;

    // Risk Context
    private String evaluationPurpose;
    private Boolean greeksEnabled;
    private String shockScenario;
    private String aggregationLevel;

    // Accounting Context
    private String accountingBook;
    private String pnlType;
    private Boolean includeAccruals;

    // Credit Context
    private String creditCurve;
    private String nettingSet;
    private String collateralAgreement;

    // Audit Context
    private String user;
    private String legalEntity;
    private String sourceSystem;

    // Getters and Setters
    public String getMarketDataSet() { return marketDataSet; }
    public void setMarketDataSet(String marketDataSet) { this.marketDataSet = marketDataSet; }

    public LocalDate getPricingDate() { return pricingDate; }
    public void setPricingDate(LocalDate pricingDate) { this.pricingDate = pricingDate; }

    public String getCurveSet() { return curveSet; }
    public void setCurveSet(String curveSet) { this.curveSet = curveSet; }

    public String getFxScenario() { return fxScenario; }
    public void setFxScenario(String fxScenario) { this.fxScenario = fxScenario; }

    public String getVolatilitySurface() { return volatilitySurface; }
    public void setVolatilitySurface(String volatilitySurface) { this.volatilitySurface = volatilitySurface; }

    public String getPricingModel() { return pricingModel; }
    public void setPricingModel(String pricingModel) { this.pricingModel = pricingModel; }

    public String getDayCount() { return dayCount; }
    public void setDayCount(String dayCount) { this.dayCount = dayCount; }

    public String getCompounding() { return compounding; }
    public void setCompounding(String compounding) { this.compounding = compounding; }

    public String getSettlementType() { return settlementType; }
    public void setSettlementType(String settlementType) { this.settlementType = settlementType; }

    public String getEvaluationPurpose() { return evaluationPurpose; }
    public void setEvaluationPurpose(String evaluationPurpose) { this.evaluationPurpose = evaluationPurpose; }

    public Boolean getGreeksEnabled() { return greeksEnabled; }
    public void setGreeksEnabled(Boolean greeksEnabled) { this.greeksEnabled = greeksEnabled; }

    public String getShockScenario() { return shockScenario; }
    public void setShockScenario(String shockScenario) { this.shockScenario = shockScenario; }

    public String getAggregationLevel() { return aggregationLevel; }
    public void setAggregationLevel(String aggregationLevel) { this.aggregationLevel = aggregationLevel; }

    public String getAccountingBook() { return accountingBook; }
    public void setAccountingBook(String accountingBook) { this.accountingBook = accountingBook; }

    public String getPnlType() { return pnlType; }
    public void setPnlType(String pnlType) { this.pnlType = pnlType; }

    public Boolean getIncludeAccruals() { return includeAccruals; }
    public void setIncludeAccruals(Boolean includeAccruals) { this.includeAccruals = includeAccruals; }

    public String getCreditCurve() { return creditCurve; }
    public void setCreditCurve(String creditCurve) { this.creditCurve = creditCurve; }

    public String getNettingSet() { return nettingSet; }
    public void setNettingSet(String nettingSet) { this.nettingSet = nettingSet; }

    public String getCollateralAgreement() { return collateralAgreement; }
    public void setCollateralAgreement(String collateralAgreement) { this.collateralAgreement = collateralAgreement; }

    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }

    public String getLegalEntity() { return legalEntity; }
    public void setLegalEntity(String legalEntity) { this.legalEntity = legalEntity; }

    public String getSourceSystem() { return sourceSystem; }
    public void setSourceSystem(String sourceSystem) { this.sourceSystem = sourceSystem; }
}
