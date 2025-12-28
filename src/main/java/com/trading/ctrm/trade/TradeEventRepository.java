package com.trading.ctrm.trade;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeEventRepository extends JpaRepository<TradeEvent, Long> {

    long countByTradeAndEventType(Trade trade, TradeEventType eventType);
    List<TradeEvent> findByTradeIdOrderByCreatedAt(Long tradeId);
}
