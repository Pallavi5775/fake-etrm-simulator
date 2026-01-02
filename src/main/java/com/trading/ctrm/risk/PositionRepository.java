package com.trading.ctrm.risk;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {

    List<Position> findByPositionDate(LocalDate positionDate);

    List<Position> findByPortfolioAndPositionDate(String portfolio, LocalDate positionDate);

    List<Position> findByCommodityAndPositionDate(String commodity, LocalDate positionDate);

    @Query("SELECT p FROM Position p WHERE p.positionDate = :date AND p.portfolio = :portfolio AND p.commodity = :commodity")
    Optional<Position> findByDatePortfolioCommodity(
        @Param("date") LocalDate date,
        @Param("portfolio") String portfolio,
        @Param("commodity") String commodity
    );

    @Query("SELECT SUM(p.netQuantity) FROM Position p WHERE p.portfolio = :portfolio AND p.positionDate = :date")
    BigDecimal getTotalNetPositionByPortfolio(@Param("portfolio") String portfolio, @Param("date") LocalDate date);

    @Query("SELECT SUM(p.netMtm) FROM Position p WHERE p.portfolio = :portfolio AND p.positionDate = :date")
    BigDecimal getTotalMtmByPortfolio(@Param("portfolio") String portfolio, @Param("date") LocalDate date);
}
