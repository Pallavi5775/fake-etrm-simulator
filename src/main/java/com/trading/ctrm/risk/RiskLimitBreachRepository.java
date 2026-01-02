package com.trading.ctrm.risk;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RiskLimitBreachRepository extends JpaRepository<RiskLimitBreach, Long> {

    List<RiskLimitBreach> findByBreachStatus(String breachStatus);

    List<RiskLimitBreach> findByLimitId(Long limitId);

    List<RiskLimitBreach> findBySeverity(String severity);

    @Query("SELECT rlb FROM RiskLimitBreach rlb WHERE rlb.breachStatus = 'ACTIVE' AND rlb.severity = :severity ORDER BY rlb.breachDate DESC")
    List<RiskLimitBreach> findActiveBreachesBySeverity(@Param("severity") String severity);

    @Query("SELECT rlb FROM RiskLimitBreach rlb WHERE rlb.breachDate >= :startDate ORDER BY rlb.breachDate DESC")
    List<RiskLimitBreach> findRecentBreaches(@Param("startDate") LocalDateTime startDate);
}
