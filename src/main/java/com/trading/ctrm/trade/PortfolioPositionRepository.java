package com.trading.ctrm.trade;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trading.ctrm.common.PortfolioPosition;
import com.trading.ctrm.instrument.Instrument;

public interface PortfolioPositionRepository
        extends JpaRepository<PortfolioPosition, Long> {

    Optional<PortfolioPosition> findByPortfolioAndInstrument(
        String portfolio,
        Instrument instrument
    );

    List<PortfolioPosition> findByPortfolio(String portfolio);
}
