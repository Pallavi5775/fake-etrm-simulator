
        
package com.trading.ctrm.trade;

import org.springframework.data.domain.Pageable;
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

    @Query("SELECT t FROM Trade t JOIN FETCH t.instrument WHERE t.status IN :statuses")
    List<Trade> findByStatusIn(List<TradeStatus> statuses);

    @Query("SELECT t FROM Trade t JOIN FETCH t.instrument WHERE t.portfolio = :portfolio AND t.status IN :statuses")
    List<Trade> findByPortfolioAndStatusIn(String portfolio, List<TradeStatus> statuses);

@Query("""
select t from Trade t
where (:desk is null or t.portfolio = :desk)
  and (:status is null or t.status = :status)
  and (:instrumentCode is null or t.instrument.instrumentCode = :instrumentCode)
  and (:counterparty is null or t.counterparty = :counterparty)
""")
List<Trade> findByFilters(
        @Param("desk") String desk,
        @Param("status") TradeStatus status,
        @Param("instrumentCode") String instrumentCode,
        @Param("counterparty") String counterparty
);


@Query("""
select t from Trade t
where (:desk is null or t.portfolio = :desk)
order by t.createdAt desc
""")
List<Trade> findSampleTrades(
        @Param("desk") String desk,
        Pageable pageable
);


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
