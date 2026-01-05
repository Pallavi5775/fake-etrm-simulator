package com.trading.ctrm.pricing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OptionForwardCurveRepository extends JpaRepository<OptionForwardCurve, Long> {

    @Query("SELECT ofc FROM OptionForwardCurve ofc WHERE ofc.instrumentCode = :instrumentCode AND ofc.deliveryDate = :deliveryDate ORDER BY ofc.curveDate DESC")
    Optional<OptionForwardCurve> findByInstrumentCodeAndDeliveryDate(String instrumentCode, LocalDate deliveryDate);

    List<OptionForwardCurve> findByInstrumentCode(String instrumentCode);

    List<OptionForwardCurve> findByDeliveryDate(LocalDate deliveryDate);

    @Query("SELECT ofc FROM OptionForwardCurve ofc WHERE ofc.instrumentCode = :instrumentCode AND ofc.deliveryDate = :deliveryDate")
    List<OptionForwardCurve> findAllByInstrumentCodeAndDeliveryDate(String instrumentCode, LocalDate deliveryDate);

    @Query("SELECT DISTINCT ofc.instrumentCode FROM OptionForwardCurve ofc")
    List<String> findDistinctInstruments();
}