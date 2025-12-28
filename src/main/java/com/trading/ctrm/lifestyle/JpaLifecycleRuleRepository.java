package com.trading.ctrm.lifestyle;

import com.trading.ctrm.trade.TradeEventType;
import com.trading.ctrm.trade.TradeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface JpaLifecycleRuleRepository
        extends JpaRepository<LifecycleRule, Long> {

    Optional<LifecycleRule> findByFromStatusAndEventTypeAndDeskAndEnabledTrue(
        TradeStatus fromStatus,
        TradeEventType eventType,
        String desk
    );
}
