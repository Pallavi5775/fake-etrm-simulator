package com.trading.ctrm.marketdata;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

public class ForwardCurveRequest {
    
    private String instrumentCode;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deliveryDate;
    private double price;

    // Constructors
    public ForwardCurveRequest() {}

    public ForwardCurveRequest(String instrumentCode, LocalDate deliveryDate, double price) {
        this.instrumentCode = instrumentCode;
        this.deliveryDate = deliveryDate;
        this.price = price;
    }

    // Getters and Setters
    public String getInstrumentCode() {
        return instrumentCode;
    }

    public void setInstrumentCode(String instrumentCode) {
        this.instrumentCode = instrumentCode;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
