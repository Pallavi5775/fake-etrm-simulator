package com.trading.ctrm.pricing;

import org.springframework.data.jpa.repository.JpaRepository;



import com.trading.ctrm.trade.Trade;

import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ValuationHistoryRepository
        extends JpaRepository<ValuationHistory, Long> {

    // 🔑 Used for PnL calculation
    @Query("""
        select v from ValuationHistory v
        where v.trade.id = :tradeId
        order by v.valuationDate desc
    """)
    List<ValuationHistory> findLatestTwo(Long tradeId);
}
