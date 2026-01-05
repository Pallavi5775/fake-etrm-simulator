package com.trading.ctrm.pricing;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "option_forward_curves", schema = "ctrm")
public class OptionForwardCurve {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String instrumentCode;

    @Column(name = "delivery_date", nullable = false)
    private LocalDate deliveryDate;

    @Column(nullable = false)
    private double forwardPrice;

    @Column(name = "curve_date")
    private LocalDate curveDate;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInstrumentCode() { return instrumentCode; }
    public void setInstrumentCode(String instrumentCode) { this.instrumentCode = instrumentCode; }

    public LocalDate getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(LocalDate deliveryDate) { this.deliveryDate = deliveryDate; }

    public double getForwardPrice() { return forwardPrice; }
    public void setForwardPrice(double forwardPrice) { this.forwardPrice = forwardPrice; }

    public LocalDate getCurveDate() { return curveDate; }
    public void setCurveDate(LocalDate curveDate) { this.curveDate = curveDate; }
}