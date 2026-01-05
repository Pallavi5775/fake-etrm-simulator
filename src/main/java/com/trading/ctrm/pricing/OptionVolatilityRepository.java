package com.trading.ctrm.pricing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface OptionVolatilityRepository extends JpaRepository<OptionVolatility, Long> {

    Optional<OptionVolatility> findByInstrumentCodeAndDate(String instrumentCode, LocalDate date);
}