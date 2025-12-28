package com.trading.ctrm.lifestyle;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trading.ctrm.trade.TradeEventType;
import com.trading.ctrm.trade.TradeStatus;

@Repository
public interface LifecycleRuleJpaRepository
        extends JpaRepository<LifecycleRuleEntity, Long> {

    Optional<LifecycleRuleEntity> findByCurrentStatusAndEventType(
            TradeStatus currentStatus,
            TradeEventType eventType
    );
}

