package com.trading.ctrm.instrument;

import com.trading.ctrm.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(
    name = "instruments",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_instrument_symbol", columnNames = "symbol")
    }
)
public class Instrument extends BaseEntity {

    @Column(name = "symbol", nullable = false, length = 50, updatable = false)
    private String symbol;

    @Enumerated(EnumType.STRING)
    @Column(name = "instrument_type", nullable = false, length = 20)
    private InstrumentType type;

    protected Instrument() {
        // JPA only
    }

    public Instrument(String symbol, InstrumentType type) {
        this.symbol = symbol;
        this.type = type;
    }

    public String getSymbol() {
        return symbol;
    }

    public InstrumentType getType() {
        return type;
    }
}
