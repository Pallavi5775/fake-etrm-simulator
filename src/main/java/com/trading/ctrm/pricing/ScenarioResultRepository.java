package com.trading.ctrm.pricing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ScenarioResultRepository extends JpaRepository<ScenarioResult, Long> {

    List<ScenarioResult> findByScenarioId(Long scenarioId);

    List<ScenarioResult> findByTradeId(Long tradeId);

    @Query("SELECT SUM(sr.pnlImpact) FROM ScenarioResult sr WHERE sr.scenarioId = :scenarioId")
    BigDecimal getTotalImpactForScenario(@Param("scenarioId") Long scenarioId);

    @Query("SELECT sr FROM ScenarioResult sr WHERE sr.scenarioId = :scenarioId ORDER BY ABS(sr.pnlImpact) DESC")
    List<ScenarioResult> findTopImpactsByScenario(@Param("scenarioId") Long scenarioId);
}
