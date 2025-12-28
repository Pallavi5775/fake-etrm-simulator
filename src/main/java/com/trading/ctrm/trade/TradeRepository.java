package com.trading.ctrm.trade;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;



public interface TradeRepository
        extends JpaRepository<Trade, Long> {      

    Optional<Trade> findByTradeId(String tradeId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Trade t where t.id = :id")
    Optional<Trade> findByIdForUpdate(@Param("id") Long id);

    List<Trade> findByPortfolio(String portfolio);

    List<Trade> findByStatusAndPendingApprovalRole(
        TradeStatus status,
        String pendingApprovalRole
);

    List<Trade> findByCounterparty(String counterparty);

    // 🔹 NEW — query by status
    List<Trade> findByStatus(TradeStatus status);

    // 🔹 NEW — portfolio + status (very common in CTRM)
    List<Trade> findByPortfolioAndStatus(
            String portfolio,
            TradeStatus status
    );

    @Query("""
select t from Trade t
where (:desk is null or t.portfolio = :desk)
  and (:status is null or t.status = :status)
  and (:instrument is null or t.instrument.symbol = :instrument)
  and (:counterparty is null or t.counterparty = :counterparty)
""")
List<Trade> findByFilters(
    String desk,
    TradeStatus status,
    String instrument,
    String counterparty
);


@Query("""
select t from Trade t
where (:desk is null or t.portfolio = :desk)
order by t.createdAt desc
""")
List<Trade> findSampleTrades(String desk, int pageable);


    // 🔹 NEW — bulk status update (Endur-style lifecycle update)
    @Modifying
    @Transactional
    @Query("""
        update Trade t
        set t.status = :status
        where t.tradeId = :tradeId
    """)
    int updateTradeStatus(
            String tradeId,
            TradeStatus status
    );
}
