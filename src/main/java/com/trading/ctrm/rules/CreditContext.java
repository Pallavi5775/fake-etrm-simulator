package com.trading.ctrm.rules;

import com.trading.ctrm.trade.Trade;

public class CreditContext {

    private final String creditCurve;
    private final String nettingSet;
    private final String collateralAgreement;

    public CreditContext(
            String creditCurve,
            String nettingSet,
            String collateralAgreement
    ) {
        this.creditCurve = creditCurve;
        this.nettingSet = nettingSet;
        this.collateralAgreement = collateralAgreement;
    }

    public String creditCurve() { return creditCurve; }
    public String nettingSet() { return nettingSet; }
    public String collateralAgreement() { return collateralAgreement; }

    public static CreditContext of(
            String creditCurve,
            String nettingSet,
            String collateralAgreement
    ) {
        return new CreditContext(
                creditCurve,
                nettingSet,
                collateralAgreement
        );
    }

    /**
     * Factory method to create CreditContext from a Trade entity with counterparty-based defaults
     */
    public static CreditContext fromTrade(Trade trade) {
        String counterparty = trade.getCounterparty();
        return new CreditContext(
                counterparty + "_CVA",
                counterparty + "_NETSET",
                "CSA_2024"
        );
    }

    /**
     * Factory method with configurable parameters from UI
     */
    public static CreditContext fromTrade(
            Trade trade,
            String creditCurve,
            String nettingSet,
            String collateralAgreement
    ) {
        String counterparty = trade.getCounterparty();
        return new CreditContext(
                creditCurve != null ? creditCurve : counterparty + "_CVA",
                nettingSet != null ? nettingSet : counterparty + "_NETSET",
                collateralAgreement != null ? collateralAgreement : "CSA_2024"
        );
    }
}