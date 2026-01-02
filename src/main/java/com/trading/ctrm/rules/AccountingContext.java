package com.trading.ctrm.rules;

import com.trading.ctrm.trade.Trade;

public class AccountingContext {

    private final String accountingBook;
    private final String pnlType;      // REALIZED / UNREALIZED
    private final boolean includeAccruals;

    public AccountingContext(
            String accountingBook,
            String pnlType,
            boolean includeAccruals
    ) {
        this.accountingBook = accountingBook;
        this.pnlType = pnlType;
        this.includeAccruals = includeAccruals;
    }

    public String accountingBook() { return accountingBook; }
    public String pnlType() { return pnlType; }
    public boolean includeAccruals() { return includeAccruals; }

    public static AccountingContext of(
            String accountingBook,
            String pnlType,
            boolean includeAccruals
    ) {
        return new AccountingContext(
                accountingBook,
                pnlType,
                includeAccruals
        );
    }

    /**
     * Factory method to create AccountingContext from a Trade entity with default accounting settings
     */
    public static AccountingContext fromTrade(Trade trade) {
        return new AccountingContext(
                "IFRS",
                "UNREALIZED",
                true
        );
    }

    /**
     * Factory method with configurable parameters from UI
     */
    public static AccountingContext fromTrade(
            Trade trade,
            String accountingBook,
            String pnlType,
            Boolean includeAccruals
    ) {
        return new AccountingContext(
                accountingBook != null ? accountingBook : "IFRS",
                pnlType != null ? pnlType : "UNREALIZED",
                includeAccruals != null ? includeAccruals : true
        );
    }
}