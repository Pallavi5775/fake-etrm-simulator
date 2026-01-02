package com.trading.ctrm.pricing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ValuationScenarioRepository extends JpaRepository<ValuationScenario, Long> {

    List<ValuationScenario> findByScenarioType(String scenarioType);

    List<ValuationScenario> findByBaseDate(LocalDate baseDate);

    List<ValuationScenario> findByCreatedBy(String createdBy);
}
