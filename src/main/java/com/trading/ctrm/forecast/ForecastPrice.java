package com.trading.ctrm.forecast;

import com.trading.ctrm.instrument.Instrument;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "forecast_price")
public class ForecastPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "instrument_id")
    private Instrument instrument;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private double value;

    public Long getId() { return id; }
    public Instrument getInstrument() { return instrument; }
    public LocalDate getDate() { return date; }
    public double getValue() { return value; }
}
