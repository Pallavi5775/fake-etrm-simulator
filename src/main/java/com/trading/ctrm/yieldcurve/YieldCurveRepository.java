package com.trading.ctrm.yieldcurve;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface YieldCurveRepository extends JpaRepository<YieldCurve, Long> {
    // Added for optimized market data lookup - replaces inefficient findAll() + filtering
    Optional<YieldCurve> findByCurveNameAndDate(String curveName, LocalDate date);
}
