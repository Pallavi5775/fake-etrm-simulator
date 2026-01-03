package com.trading.ctrm.marketdata;

import java.time.LocalDate;

public class ForwardCurveResponse {
    
    private Long id;
    private String instrumentCode;
    private LocalDate deliveryDate;
    private double price;
    private LocalDate curveDate;

    // Constructors
    public ForwardCurveResponse() {}

    public ForwardCurveResponse(Long id, String instrumentCode, LocalDate deliveryDate, 
                                double price, LocalDate curveDate) {
        this.id = id;
        this.instrumentCode = instrumentCode;
        this.deliveryDate = deliveryDate;
        this.price = price;
        this.curveDate = curveDate;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public LocalDate getCurveDate() {
        return curveDate;
    }

    public void setCurveDate(LocalDate curveDate) {
        this.curveDate = curveDate;
    }
}
