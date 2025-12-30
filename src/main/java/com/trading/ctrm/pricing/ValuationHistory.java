package com.trading.ctrm.pricing;

import com.trading.ctrm.trade.Trade;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "valuation_history")
public class ValuationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Trade trade;

    private BigDecimal mtm;

    private LocalDate valuationDate;

    protected ValuationHistory() {
    }

    public ValuationHistory(Trade trade, BigDecimal mtm, LocalDate valuationDate) {
        this.trade = trade;
        this.mtm = mtm;
        this.valuationDate = valuationDate;
    }

    public BigDecimal getMtm() {
        return mtm;
    }

    public LocalDate getValuationDate() {
        return valuationDate;
    }
}
