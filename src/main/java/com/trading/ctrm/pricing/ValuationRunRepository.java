package com.trading.ctrm.pricing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ValuationRunRepository extends JpaRepository<ValuationRun, Long> {

    List<ValuationRun> findByValuationDate(LocalDate valuationDate);

    List<ValuationRun> findByStatus(String status);

    Optional<ValuationRun> findTopByValuationDateAndStatusOrderByStartedAtDesc(LocalDate valuationDate, String status);

    List<ValuationRun> findTop10ByOrderByStartedAtDesc();
}
