package com.trading.ctrm.trade;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TradeVersionRepository extends JpaRepository<TradeVersion, Long> {

    List<TradeVersion> findByTradeIdOrderByVersionNumberAsc(Long tradeId);

    Optional<TradeVersion> findByTradeIdAndVersionNumber(Long tradeId, Integer versionNumber);

    @Query("SELECT tv FROM TradeVersion tv WHERE tv.tradeId = :tradeId ORDER BY tv.versionNumber DESC LIMIT 1")
    Optional<TradeVersion> findLatestVersion(@Param("tradeId") Long tradeId);

    List<TradeVersion> findByAmendedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<TradeVersion> findByAmendedBy(String amendedBy);

    @Query("SELECT COUNT(tv) FROM TradeVersion tv WHERE tv.tradeId = :tradeId")
    Integer countVersionsByTradeId(@Param("tradeId") Long tradeId);
}
