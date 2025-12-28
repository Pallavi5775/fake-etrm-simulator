package com.trading.ctrm.trade.dto;

public class PositionResponseDto {

    private String portfolio;
    private String instrumentSymbol;
    private double netQuantity;

    public PositionResponseDto() {}

    public PositionResponseDto(
            String portfolio,
            String instrumentSymbol,
            double netQuantity
    ) {
        this.portfolio = portfolio;
        this.instrumentSymbol = instrumentSymbol;
        this.netQuantity = netQuantity;
    }

    public String getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(String portfolio) {
        this.portfolio = portfolio;
    }

    public String getInstrumentSymbol() {
        return instrumentSymbol;
    }

    public void setInstrumentSymbol(String instrumentSymbol) {
        this.instrumentSymbol = instrumentSymbol;
    }

    public double getNetQuantity() {
        return netQuantity;
    }

    public void setNetQuantity(double netQuantity) {
        this.netQuantity = netQuantity;
    }
}

