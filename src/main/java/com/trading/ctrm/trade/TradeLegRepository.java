package com.trading.ctrm.trade;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeLegRepository extends JpaRepository<TradeLeg, Long> {

    List<TradeLeg> findByTradeIdOrderByLegNumber(String tradeId);

    void deleteByTradeId(String tradeId);
}
