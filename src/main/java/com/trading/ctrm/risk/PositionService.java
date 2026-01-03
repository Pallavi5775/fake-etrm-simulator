package com.trading.ctrm.risk;

import com.trading.ctrm.trade.Trade;
import com.trading.ctrm.trade.TradeRepository;
import com.trading.ctrm.pricing.ValuationResult;
import com.trading.ctrm.pricing.ValuationResultRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Position Service - Endur-style position aggregation
 * Aggregates trades into positions by portfolio, commodity, delivery period
 */
@Service
public class PositionService {

    private static final Logger log = LoggerFactory.getLogger(PositionService.class);

    private final TradeRepository tradeRepository;
    private final PositionRepository positionRepository;
    private final ValuationResultRepository valuationResultRepository;

    public PositionService(
            TradeRepository tradeRepository,
            PositionRepository positionRepository,
            ValuationResultRepository valuationResultRepository) {
        this.tradeRepository = tradeRepository;
        this.positionRepository = positionRepository;
        this.valuationResultRepository = valuationResultRepository;
    }

    /**
     * Calculate positions for a date
     */
    @Transactional
    public void calculatePositions(LocalDate positionDate, String portfolioFilter) {
        log.info("Calculating positions for date: {}, portfolio: {}", positionDate, portfolioFilter);

        // Get all active trades
        List<Trade> trades = getActiveTrades(portfolioFilter);
        log.info("Found {} active trades", trades.size());

        // Clear existing positions for this date
        List<Position> existingPositions = portfolioFilter == null || portfolioFilter.equals("ALL")
            ? positionRepository.findByPositionDate(positionDate)
            : positionRepository.findByPortfolioAndPositionDate(portfolioFilter, positionDate);

        positionRepository.deleteAll(existingPositions);

        // Aggregate trades into positions
        for (Trade trade : trades) {
            aggregateTrade(trade, positionDate);
        }

        log.info("Position calculation completed");
    }

    /**
     * Aggregate single trade into positions
     */
    private void aggregateTrade(Trade trade, LocalDate positionDate) {
        String portfolio = trade.getPortfolio();
        String commodity = trade.getInstrument().getInstrumentType().name(); // Use instrument type as commodity
        LocalDate deliveryStart = positionDate; // Simplified - use position date
        LocalDate deliveryEnd = positionDate.plusMonths(1); // Simplified

        // Find or create position
        Optional<Position> positionOpt = positionRepository.findByDatePortfolioCommodity(
            positionDate, portfolio, commodity
        );

        Position position = positionOpt.orElse(new Position());
        position.setPositionDate(positionDate);
        position.setPortfolio(portfolio);
        position.setCommodity(commodity);
        position.setDeliveryStart(deliveryStart);
        position.setDeliveryEnd(deliveryEnd);

        // Get valuation
        Optional<ValuationResult> valuationOpt = valuationResultRepository
            .findByTradeIdAndPricingDate(trade.getId(), positionDate);

        BigDecimal mtm = valuationOpt.map(ValuationResult::getMtmTotal).orElse(BigDecimal.ZERO);
        BigDecimal delta = valuationOpt.map(ValuationResult::getDelta).orElse(BigDecimal.ZERO);
        BigDecimal gamma = valuationOpt.map(ValuationResult::getGamma).orElse(BigDecimal.ZERO);
        BigDecimal vega = valuationOpt.map(ValuationResult::getVega).orElse(BigDecimal.ZERO);

        // Aggregate quantities
        BigDecimal quantity = trade.getQuantity();
        if (trade.getBuySell().name().equals("BUY")) {
            position.setLongQuantity(position.getLongQuantity().add(quantity));
            position.setLongMtm(position.getLongMtm().add(mtm));
        } else {
            position.setShortQuantity(position.getShortQuantity().add(quantity));
            position.setShortMtm(position.getShortMtm().add(mtm));
        }

        position.setNetQuantity(position.getLongQuantity().subtract(position.getShortQuantity()));
        position.setNetMtm(position.getLongMtm().add(position.getShortMtm()));

        // Aggregate risk metrics
        position.setDelta(position.getDelta().add(delta));
        position.setGamma(position.getGamma().add(gamma));
        position.setVega(position.getVega().add(vega));

        position.setTradeCount(position.getTradeCount() + 1);
        position.setLastUpdated(LocalDateTime.now());

        positionRepository.save(position);
    }

    /**
     * Get active trades for aggregation
     */
    private List<Trade> getActiveTrades(String portfolioFilter) {
        java.util.List<com.trading.ctrm.trade.TradeStatus> eligibleStatuses = java.util.Arrays.asList(
            com.trading.ctrm.trade.TradeStatus.CREATED,
            com.trading.ctrm.trade.TradeStatus.VALIDATED,
            com.trading.ctrm.trade.TradeStatus.PENDING_APPROVAL,
            com.trading.ctrm.trade.TradeStatus.APPROVED,
            com.trading.ctrm.trade.TradeStatus.BOOKED
        );
        java.util.List<Trade> trades = new java.util.ArrayList<>();
        if (portfolioFilter == null || portfolioFilter.equals("ALL")) {
            for (com.trading.ctrm.trade.TradeStatus status : eligibleStatuses) {
                trades.addAll(tradeRepository.findByStatus(status));
            }
        } else {
            for (com.trading.ctrm.trade.TradeStatus status : eligibleStatuses) {
                trades.addAll(tradeRepository.findByPortfolioAndStatus(portfolioFilter, status));
            }
        }
        return trades;
    }

    /**
     * Get positions for a date
     */
    public List<Position> getPositions(LocalDate positionDate) {
        return positionRepository.findByPositionDate(positionDate);
    }

    /**
     * Get portfolio positions
     */
    public List<Position> getPortfolioPositions(String portfolio, LocalDate positionDate) {
        return positionRepository.findByPortfolioAndPositionDate(portfolio, positionDate);
    }

    /**
     * Get commodity positions
     */
    public List<Position> getCommodityPositions(String commodity, LocalDate positionDate) {
        return positionRepository.findByCommodityAndPositionDate(commodity, positionDate);
    }

    /**
     * Get total portfolio exposure
     */
    public BigDecimal getPortfolioNetPosition(String portfolio, LocalDate positionDate) {
        BigDecimal total = positionRepository.getTotalNetPositionByPortfolio(portfolio, positionDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Get total portfolio MTM
     */
    public BigDecimal getPortfolioMtm(String portfolio, LocalDate positionDate) {
        BigDecimal total = positionRepository.getTotalMtmByPortfolio(portfolio, positionDate);
        return total != null ? total : BigDecimal.ZERO;
    }
}
