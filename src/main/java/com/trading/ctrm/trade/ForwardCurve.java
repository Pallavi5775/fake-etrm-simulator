package com.trading.ctrm.trade;

import com.trading.ctrm.instrument.Instrument;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "forward_curves")
public class ForwardCurve {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "instrument_id")
    private Instrument instrument;   // ✅ ENTITY

    @Column(nullable = false)
    private LocalDate deliveryDate;

    private double price;

    public Instrument getInstrument() {
        return instrument;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public double getPrice() {
        return price;
    }
}
