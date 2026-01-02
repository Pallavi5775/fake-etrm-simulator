package com.trading.ctrm.rules;

import com.trading.ctrm.trade.Trade;

public class TradeContext {

    private final long tradeId;
    private final double quantity;
    private final String counterparty;
    private final String portfolio;
    private final String instrumentType;
    private final String buySell;

    public TradeContext(
            long tradeId,
            double quantity,
            String counterparty,
            String portfolio,
            String instrumentType,
            String buySell
    ) {
        this.tradeId = tradeId;
        this.quantity = quantity;
        this.counterparty = counterparty;
        this.portfolio = portfolio;
        this.instrumentType = instrumentType;
        this.buySell = buySell;
    }

    public long tradeId() { return tradeId; }
    public double quantity() { return quantity; }
    public String counterparty() { return counterparty; }
    public String portfolio() { return portfolio; }
    public String instrumentType() { return instrumentType; }
    public String buySell() { return buySell; }

    public static TradeContext of(
            long tradeId,
            double quantity,
            String counterparty,
            String portfolio,
            String instrumentType,
            String buySell
    ) {
        return new TradeContext(
                tradeId,
                quantity,
                counterparty,
                portfolio,
                instrumentType,
                buySell
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
                trade.getBuySell().name()
        );
    }
}
