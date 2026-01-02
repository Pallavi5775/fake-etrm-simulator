package com.trading.ctrm.risk;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RiskLimitRepository extends JpaRepository<RiskLimit, Long> {

    List<RiskLimit> findByActiveTrue();

    List<RiskLimit> findByLimitType(String limitType);

    List<RiskLimit> findByLimitScopeAndScopeValue(String limitScope, String scopeValue);

    @Query("SELECT rl FROM RiskLimit rl WHERE rl.limitType = :type AND rl.limitScope = :scope AND rl.scopeValue = :value AND rl.active = true")
    Optional<RiskLimit> findActiveLimit(
        @Param("type") String type,
        @Param("scope") String scope,
        @Param("value") String value
    );
}
