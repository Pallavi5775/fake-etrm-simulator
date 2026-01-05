package com.trading.ctrm.trade;

import com.trading.ctrm.instrument.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ForwardCurveRepository
        extends JpaRepository<ForwardCurve, Long> {

    Optional<ForwardCurve> findByInstrumentAndDeliveryDate(
            Instrument instrument,
            LocalDate deliveryDate
    );

    @Query("SELECT fc FROM ForwardCurve fc WHERE fc.instrument.instrumentCode = :instrumentCode AND fc.deliveryDate = :deliveryDate ORDER BY fc.curveDate DESC")
    Optional<ForwardCurve> findByInstrumentCodeAndDeliveryDate(
            String instrumentCode,
            LocalDate deliveryDate
    );

    // Debug query to check what data exists
    @Query("SELECT fc FROM ForwardCurve fc WHERE fc.instrument.instrumentCode = :instrumentCode")
    List<ForwardCurve> findByInstrumentCode(String instrumentCode);

    @Query("SELECT fc FROM ForwardCurve fc WHERE fc.deliveryDate = :deliveryDate")
    List<ForwardCurve> findByDeliveryDate(LocalDate deliveryDate);

    @Query("SELECT fc FROM ForwardCurve fc WHERE fc.instrument.instrumentCode = :instrumentCode AND fc.deliveryDate = :deliveryDate")
    List<ForwardCurve> findAllByInstrumentCodeAndDeliveryDate(String instrumentCode, LocalDate deliveryDate);

    @Query("SELECT fc FROM ForwardCurve fc WHERE fc.instrument = :instrument AND fc.deliveryDate = :deliveryDate ORDER BY fc.curveDate DESC LIMIT 1")
    Optional<ForwardCurve> findLatestByInstrumentAndDeliveryDate(
            Instrument instrument,
            LocalDate deliveryDate
    );

    List<ForwardCurve> findByInstrumentOrderByDeliveryDate(Instrument instrument);

    @Query("SELECT DISTINCT i.instrumentCode FROM ForwardCurve fc JOIN fc.instrument i")
    List<String> findDistinctInstruments();
}

