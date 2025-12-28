package com.trading.ctrm.trade;

import com.trading.ctrm.instrument.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface ForwardCurveRepository
        extends JpaRepository<ForwardCurve, Long> {

    Optional<ForwardCurve> findByInstrumentAndDeliveryDate(
            Instrument instrument,
            LocalDate deliveryDate
    );
}

