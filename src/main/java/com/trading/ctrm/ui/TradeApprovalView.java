package com.trading.ctrm.ui;

import java.math.BigDecimal;

import com.trading.ctrm.trade.Trade;

public class TradeApprovalView {

    private Long tradeId;
    private String instrumentCode;
    private String instrumentType;
    private BigDecimal quantity;
    private BigDecimal tradePrice;
    private BigDecimal mtm;
    private String pendingRole;

    public static TradeApprovalView from(Trade trade) {
        TradeApprovalView view = new TradeApprovalView();
        view.tradeId = trade.getId();
        view.instrumentCode = trade.getInstrument().getInstrumentCode();
        view.instrumentType = trade.getInstrument().getInstrumentType().name();
        view.quantity = trade.getQuantity();
        view.tradePrice = trade.getPrice();
        view.mtm = trade.getMtm(); // assumes MTM is stored on Trade
        view.pendingRole = trade.getPendingApprovalRole();
        return view;
    }

    /* Getters */

    public Long getTradeId() {
        return tradeId;
    }

    public String getInstrumentCode() {
        return instrumentCode;
    }

    public String getInstrumentType() {
        return instrumentType;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getTradePrice() {
        return tradePrice;
    }

    public BigDecimal getMtm() {
        return mtm;
    }

    public String getPendingRole() {
        return pendingRole;
    }
}
