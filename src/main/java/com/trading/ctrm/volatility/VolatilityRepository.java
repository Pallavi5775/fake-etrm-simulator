package com.trading.ctrm.volatility;

import com.trading.ctrm.instrument.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface VolatilityRepository extends JpaRepository<Volatility, Long> {
    Optional<Volatility> findByInstrumentAndDate(Instrument instrument, LocalDate date);
}
