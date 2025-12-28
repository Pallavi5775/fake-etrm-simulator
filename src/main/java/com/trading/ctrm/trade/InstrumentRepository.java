package com.trading.ctrm.trade;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trading.ctrm.instrument.Instrument;

import java.util.Optional;

public interface InstrumentRepository
        extends JpaRepository<Instrument, Long> {

    /**
     * Find instrument by unique symbol
     * Used by:
     *  - Trade booking
     *  - Pricing
     *  - Position aggregation
     */
    Optional<Instrument> findBySymbol(String symbol);

    /**
     * Check if instrument already exists
     * Useful for reference data load
     */
    boolean existsBySymbol(String symbol);
}
