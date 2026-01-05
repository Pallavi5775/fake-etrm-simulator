package com.trading.ctrm.forecast;

import com.trading.ctrm.instrument.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ForecastPriceRepository extends JpaRepository<ForecastPrice, Long> {
    Optional<ForecastPrice> findByInstrumentAndDate(Instrument instrument, LocalDate date);
}
