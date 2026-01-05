package com.trading.ctrm.rules;

import java.time.LocalDate;

public class ValuationContext {

    private final TradeContext trade;
    private final MarketContext market;
    private final PricingContext pricing;
    private final RiskContext risk;
    private final AccountingContext accounting;
    private final CreditContext credit;
    private final AuditContext audit;
    private final LocalDate valuationDate;

    private ValuationContext(Builder builder) {
        this.trade = builder.trade;
        this.market = builder.market;
        this.pricing = builder.pricing;
        this.risk = builder.risk;
        this.accounting = builder.accounting;
        this.credit = builder.credit;
        this.audit = builder.audit;
        this.valuationDate = builder.valuationDate;
    }

    public TradeContext trade() { return trade; }
    public MarketContext market() { return market; }
    public PricingContext pricing() { return pricing; }
    public RiskContext risk() { return risk; }
    public AccountingContext accounting() { return accounting; }
    public CreditContext credit() { return credit; }
    public AuditContext audit() { return audit; }
    public LocalDate valuationDate() { return valuationDate; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private TradeContext trade;
        private MarketContext market;
        private PricingContext pricing;
        private RiskContext risk;
        private AccountingContext accounting;
        private CreditContext credit;
        private AuditContext audit;
        private LocalDate valuationDate;

        public Builder trade(TradeContext trade) {
            this.trade = trade;
            return this;
        }

        public Builder market(MarketContext market) {
            this.market = market;
            return this;
        }

        public Builder pricing(PricingContext pricing) {
            this.pricing = pricing;
            return this;
        }

        public Builder risk(RiskContext risk) {
            this.risk = risk;
            return this;
        }

        public Builder accounting(AccountingContext accounting) {
            this.accounting = accounting;
            return this;
        }

        public Builder credit(CreditContext credit) {
            this.credit = credit;
            return this;
        }

        public Builder audit(AuditContext audit) {
            this.audit = audit;
            return this;
        }

        public Builder valuationDate(LocalDate valuationDate) {
            this.valuationDate = valuationDate;
            return this;
        }

        public ValuationContext build() {
            return new ValuationContext(this);
        }
    }
}
