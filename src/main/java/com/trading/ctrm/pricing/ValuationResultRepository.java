package com.trading.ctrm.pricing;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ValuationResultRepository extends JpaRepository<ValuationResult, Long> {
    
    Optional<ValuationResult> findByTradeIdAndPricingDate(Long tradeId, LocalDate pricingDate);
    
    List<ValuationResult> findByPricingDate(LocalDate pricingDate);
    
    List<ValuationResult> findByTradeId(Long tradeId);
    
    List<ValuationResult> findByValuationRunId(Long runId);
}
