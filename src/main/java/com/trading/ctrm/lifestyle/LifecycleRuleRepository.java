package com.trading.ctrm.lifestyle;

import com.trading.ctrm.trade.TradeEventType;
import com.trading.ctrm.trade.TradeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LifecycleRuleRepository
        extends JpaRepository<LifecycleRule, Long> {

    @Query("""
        SELECT r FROM LifecycleRule r
        WHERE r.enabled = true
          AND r.fromStatus = :fromStatus
          AND r.eventType = :eventType
          AND r.desk = :desk
    """)
    Optional<LifecycleRule> findRule(
            @Param("fromStatus") TradeStatus fromStatus,
            @Param("eventType") TradeEventType eventType,
            @Param("desk") String desk
    );

    @Query("""
        select r from LifecycleRule r
        where r.fromState = :fromState
          and r.event = :event
          and r.enabled = true
          and r.productionEnabled = true
        order by r.id
    """)
    List<LifecycleRule> findEligibleRules(
        @Param("fromState") TradeStatus fromState,
        @Param("event") String event
    );
}
