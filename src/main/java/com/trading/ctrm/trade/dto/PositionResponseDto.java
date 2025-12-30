package com.trading.ctrm.trade.dto;

import java.math.BigDecimal;

public class PositionResponseDto {

    private String portfolio;
    private String instrumentSymbol;
    private BigDecimal netQuantity;

    public PositionResponseDto() {}

    public PositionResponseDto(
            String portfolio,
            String instrumentSymbol,
            BigDecimal netQuantity
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

    public BigDecimal getNetQuantity() {
        return netQuantity;
    }

    public void setNetQuantity(BigDecimal netQuantity) {
        this.netQuantity = netQuantity;
    }
}

