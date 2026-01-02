package com.trading.ctrm.pricing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.Optional;

public interface MarketCurveRepository extends JpaRepository<MarketCurve, Long> {
    
    @Query("SELECT c FROM MarketCurve c WHERE c.curveName = :curveName AND c.pricingDate = :pricingDate")
    Optional<MarketCurve> findByCurveNameAndDate(
        @Param("curveName") String curveName,
        @Param("pricingDate") LocalDate pricingDate
    );
    
    @Query("SELECT c FROM MarketCurve c WHERE c.curveName = :curveName AND c.pricingDate <= :date ORDER BY c.pricingDate DESC LIMIT 1")
    Optional<MarketCurve> findLatestCurve(
        @Param("curveName") String curveName,
        @Param("date") LocalDate date
    );
}
