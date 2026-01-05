package com.trading.ctrm.rules;

import com.trading.ctrm.trade.Trade;

public class TradeContext {

    private final long tradeId;
    private final double quantity;
    private final String counterparty;
    private final String portfolio;
    private final String instrumentType;
    private final String buySell;
    private final String commodity;
    private final java.math.BigDecimal mtm;
    private final java.math.BigDecimal price;

    public TradeContext(
            long tradeId,
            double quantity,
            String counterparty,
            String portfolio,
            String instrumentType,
            String buySell,
            String commodity,
            java.math.BigDecimal mtm,
            java.math.BigDecimal price
    ) {
        this.tradeId = tradeId;
        this.quantity = quantity;
        this.counterparty = counterparty;
        this.portfolio = portfolio;
        this.instrumentType = instrumentType;
        this.buySell = buySell;
        this.commodity = commodity;
        this.mtm = mtm;
        this.price = price;
    }

    public long tradeId() { return tradeId; }
    public double quantity() { return quantity; }
    public String counterparty() { return counterparty; }
    public String portfolio() { return portfolio; }
    public String instrumentType() { return instrumentType; }
    public String buySell() { return buySell; }
    public String commodity() { return commodity; }
    public java.math.BigDecimal mtm() { return mtm; }
    public java.math.BigDecimal price() { return price; }

    public static TradeContext of(
            long tradeId,
            double quantity,
            String counterparty,
            String portfolio,
            String instrumentType,
            String buySell,
            String commodity,
            java.math.BigDecimal mtm,
            java.math.BigDecimal price
    ) {
        return new TradeContext(
                tradeId,
                quantity,
                counterparty,
                portfolio,
                instrumentType,
                buySell,
                commodity,
                mtm,
                price
        );
    }

    /**
     * Factory method to create TradeContext from a Trade entity
     */
    public static TradeContext fromTrade(Trade trade) {
        return new TradeContext(
                trade.getId(),
                trade.getQuantity().doubleValue(),
                trade.getCounterparty(),
                trade.getPortfolio(),
                trade.getInstrument().getInstrumentType().name(),
                trade.getBuySell().name(),
                trade.getInstrument().getCommodity(),
                trade.getMtm(),
                trade.getPrice()
        );
    }
}
