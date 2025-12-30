package com.trading.ctrm.trade;

import java.math.BigDecimal;

import jakarta.persistence.*;

@Entity
public class CreditLimit {

    @Id
    @GeneratedValue
    private Long id;

    private String counterparty;

    private BigDecimal limitAmount;

    public String getCounterparty() {
        return counterparty;
    }

    public BigDecimal getLimitAmount() {
        return limitAmount;
    }
}
