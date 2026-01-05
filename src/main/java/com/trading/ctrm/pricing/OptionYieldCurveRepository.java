package com.trading.ctrm.pricing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface OptionYieldCurveRepository extends JpaRepository<OptionYieldCurve, Long> {

    Optional<OptionYieldCurve> findByCurveNameAndDate(String curveName, LocalDate date);
}