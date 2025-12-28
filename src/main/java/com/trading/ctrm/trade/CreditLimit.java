package com.trading.ctrm.trade;

import jakarta.persistence.*;

@Entity
public class CreditLimit {

    @Id
    @GeneratedValue
    private Long id;

    private String counterparty;

    private double limitAmount;

    public String getCounterparty() {
        return counterparty;
    }

    public double getLimitAmount() {
        return limitAmount;
    }
}
