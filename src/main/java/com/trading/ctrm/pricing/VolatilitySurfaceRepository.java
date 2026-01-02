package com.trading.ctrm.pricing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VolatilitySurfaceRepository extends JpaRepository<VolatilitySurface, Long> {

    @Query("SELECT v FROM VolatilitySurface v WHERE v.underlying = :underlying AND v.pricingDate = :pricingDate AND v.surfaceType = :surfaceType")
    Optional<VolatilitySurface> findByUnderlyingAndPricingDateAndType(
        @Param("underlying") String underlying,
        @Param("pricingDate") LocalDate pricingDate,
        @Param("surfaceType") String surfaceType
    );

    List<VolatilitySurface> findByPricingDate(LocalDate pricingDate);

    List<VolatilitySurface> findByUnderlying(String underlying);
}
