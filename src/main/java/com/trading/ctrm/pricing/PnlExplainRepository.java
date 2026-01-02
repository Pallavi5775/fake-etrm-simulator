package com.trading.ctrm.pricing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PnlExplainRepository extends JpaRepository<PnlExplain, Long> {

    Optional<PnlExplain> findByTradeIdAndPnlDate(Long tradeId, LocalDate pnlDate);

    List<PnlExplain> findByPnlDate(LocalDate pnlDate);

    List<PnlExplain> findByTradeIdAndPnlDateBetween(Long tradeId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT SUM(p.totalPnl) FROM PnlExplain p WHERE p.pnlDate = :pnlDate")
    BigDecimal getTotalPnlForDate(@Param("pnlDate") LocalDate pnlDate);

    @Query("SELECT p FROM PnlExplain p WHERE p.pnlDate = :pnlDate AND ABS(p.unexplained) > :threshold")
    List<PnlExplain> findHighUnexplainedPnl(@Param("pnlDate") LocalDate pnlDate, @Param("threshold") BigDecimal threshold);
}
