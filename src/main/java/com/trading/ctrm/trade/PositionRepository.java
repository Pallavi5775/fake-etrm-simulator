package com.trading.ctrm.trade;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trading.ctrm.instrument.Instrument;

public interface PositionRepository extends JpaRepository<Position, Long> {

    List<Position> findByPortfolio(String portfolio);

    Optional<Position> findByPortfolioAndInstrument(
        String portfolio,
        Instrument instrument
    );
}
