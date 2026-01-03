package com.trading.ctrm.trade;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trading.ctrm.instrument.Instrument;

import java.util.Optional;

public interface InstrumentRepository extends JpaRepository<Instrument, Long> {

    boolean existsByInstrumentCode(String instrumentCode);

    Instrument findByInstrumentCode(String instrumentCode);
    
    Optional<Instrument> findOptionalByInstrumentCode(String instrumentCode);
}