package com.trading.ctrm.pricecurve;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface PriceCurveRepository extends JpaRepository<PriceCurve, Long> {

    @Query("SELECT p FROM PriceCurve p WHERE p.curveName = :curveName AND p.date = :date")
    Optional<PriceCurve> findByCurveNameAndDate(@Param("curveName") String curveName, @Param("date") LocalDate date);
}
