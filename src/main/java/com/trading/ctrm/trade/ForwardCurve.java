package com.trading.ctrm.trade;

import com.trading.ctrm.instrument.Instrument;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "forward_curve", schema = "ctrm")
public class ForwardCurve {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "instrument_id")
    private Instrument instrument;

    @Column(nullable = false, name = "delivery_date")
    private LocalDate deliveryDate;

    @Column(nullable = false)
    private double price;

    @Column(name = "curve_date")
    private LocalDate curveDate;

    // Getters
    public Long getId() {
        return id;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public double getPrice() {
        return price;
    }

    public LocalDate getCurveDate() {
        return curveDate;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setCurveDate(LocalDate curveDate) {
        this.curveDate = curveDate;
    }
}
