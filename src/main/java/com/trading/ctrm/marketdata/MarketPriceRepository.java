package com.trading.ctrm.marketdata;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MarketPriceRepository
        extends JpaRepository<MarketPrice, Long> {

    Optional<MarketPrice> findByInstrumentCode(String instrumentCode);
}
