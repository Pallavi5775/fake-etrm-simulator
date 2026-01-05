package com.trading.ctrm.pricing;

import com.trading.ctrm.trade.Trade;
import com.trading.ctrm.trade.TradeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * P&L Attribution Service - Endur-style P&L explanation
 * Attributes daily P&L to risk factors
 */
@Service
public class PnlAttributionService {

    private static final Logger log = LoggerFactory.getLogger(PnlAttributionService.class);

    private final TradeRepository tradeRepository;
    private final ValuationResultRepository valuationResultRepository;
    private final PnlExplainRepository pnlExplainRepository;
    private final PricingService pricingService;

    public PnlAttributionService(
            TradeRepository tradeRepository,
            ValuationResultRepository valuationResultRepository,
            PnlExplainRepository pnlExplainRepository,
            PricingService pricingService) {
        this.tradeRepository = tradeRepository;
        this.valuationResultRepository = valuationResultRepository;
        this.pnlExplainRepository = pnlExplainRepository;
        this.pricingService = pricingService;
    }

    /**
     * Calculate daily P&L for all trades
     */
    @Transactional
    public void calculateDailyPnl(LocalDate pnlDate) {
        log.info("Calculating P&L for date: {}", pnlDate);

        LocalDate previousDate = pnlDate.minusDays(1);

        // Get all trades with statuses eligible for P&L calculation (including pre-approval)
        List<com.trading.ctrm.trade.TradeStatus> eligibleStatuses = java.util.Arrays.asList(
            com.trading.ctrm.trade.TradeStatus.CREATED,
            com.trading.ctrm.trade.TradeStatus.VALIDATED,
            com.trading.ctrm.trade.TradeStatus.PENDING_APPROVAL,
            com.trading.ctrm.trade.TradeStatus.APPROVED,
            com.trading.ctrm.trade.TradeStatus.BOOKED
        );
        
        List<Trade> trades = tradeRepository.findByStatusIn(eligibleStatuses);
        log.info("Total trades to calculate P&L for (statuses: {}): {}", eligibleStatuses, trades.size());

        for (Trade trade : trades) {
            try {
                log.info("Calculating P&L for trade: {} (database id: {})", trade.getTradeId(), trade.getId());
                calculateTradePnl(trade, pnlDate, previousDate);
            } catch (Exception e) {
                log.error("Failed to calculate P&L for trade: {}", trade.getTradeId(), e);
            }
        }

        log.info("P&L calculation completed for {} trades", trades.size());
    }

    /**
     * Calculate P&L for a single trade
     */
    @Transactional
    public PnlExplain calculateTradePnl(Trade trade, LocalDate pnlDate, LocalDate previousDate) {
        log.info("Calculating P&L for trade: {} (T0: {}, T1: {})", trade.getTradeId(), previousDate, pnlDate);

        try {
            // Find stored valuation results for T0 (previous date) and T1 (current date)
            Optional<ValuationResult> valuationT0 = valuationResultRepository.findTopByTradeIdAndPricingDateOrderByValuationRunIdDesc(trade.getId(), previousDate);
            Optional<ValuationResult> valuationT1 = valuationResultRepository.findTopByTradeIdAndPricingDateOrderByValuationRunIdDesc(trade.getId(), pnlDate);

            log.info("Trade {} - Looking for valuations: tradeId={}, T0_date={}, T1_date={}", 
                     trade.getTradeId(), trade.getId(), previousDate, pnlDate);
            log.debug("Trade {} - Database query results: T0_found={}, T1_found={}", 
                     trade.getTradeId(), valuationT0.isPresent(), valuationT1.isPresent());
            
            if (valuationT0.isPresent()) {
                log.debug("Trade {} - T0 valuation details: id={}, mtm={}, date={}", 
                         trade.getTradeId(), valuationT0.get().getResultId(), valuationT0.get().getMtmTotal(), valuationT0.get().getPricingDate());
            }
            if (valuationT1.isPresent()) {
                log.debug("Trade {} - T1 valuation details: id={}, mtm={}, date={}", 
                         trade.getTradeId(), valuationT1.get().getResultId(), valuationT1.get().getMtmTotal(), valuationT1.get().getPricingDate());
            }

            BigDecimal mtmT0 = valuationT0.map(ValuationResult::getMtmTotal).orElse(BigDecimal.ZERO);
            BigDecimal mtmT1 = valuationT1.map(ValuationResult::getMtmTotal).orElse(BigDecimal.ZERO);
            
            BigDecimal totalPnl = mtmT1.subtract(mtmT0);

            log.info("Trade {} P&L calculation - T0 MTM: {}, T1 MTM: {}, P&L: {}", 
                     trade.getTradeId(), mtmT0, mtmT1, totalPnl);

            // Create P&L explain
            PnlExplain pnl = new PnlExplain();
            pnl.setTradeId(trade.getId());
            pnl.setTrade(trade); // Set the trade reference for status access
            pnl.setPnlDate(pnlDate);
            pnl.setTotalPnl(totalPnl);
            
            // Set realized vs unrealized P&L based on trade status
            if (trade.getStatus() == com.trading.ctrm.trade.TradeStatus.SETTLED) {
                pnl.setRealizedPnl(totalPnl);
                pnl.setUnrealizedPnl(BigDecimal.ZERO);
            } else {
                pnl.setRealizedPnl(BigDecimal.ZERO);
                pnl.setUnrealizedPnl(totalPnl);
            }
            
            pnl.setPnlSpotMove(totalPnl); // All P&L attributed to spot move for now
            pnl.setUnexplained(BigDecimal.ZERO);
            
            // Set valuation references
            valuationT0.ifPresent(val -> pnl.setValuationT0(val.getResultId()));
            valuationT1.ifPresent(val -> pnl.setValuationT1(val.getResultId()));

            // Save P&L explain
            pnlExplainRepository.save(pnl);

            log.info("Trade {} P&L saved with valuation refs - T0: {}, T1: {}", 
                     trade.getTradeId(), 
                     valuationT0.map(ValuationResult::getResultId).orElse(null),
                     valuationT1.map(ValuationResult::getResultId).orElse(null));
            return pnl;
            
        } catch (Exception e) {
            log.error("Failed to calculate P&L for trade {}: {}", trade.getTradeId(), e.getMessage());
            return null;
        }
    }

    /**
     * Get P&L for a specific date
     */
    public List<PnlExplain> getPnlForDate(LocalDate pnlDate) {
        return pnlExplainRepository.findByPnlDate(pnlDate);
    }

    /**
     * Get total P&L for a date
     */
    public BigDecimal getTotalPnl(LocalDate pnlDate) {
        BigDecimal total = pnlExplainRepository.getTotalPnlForDate(pnlDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Get trades with high unexplained P&L
     */
    public List<PnlExplain> getHighUnexplainedPnl(LocalDate pnlDate, BigDecimal threshold) {
        return pnlExplainRepository.findHighUnexplainedPnl(pnlDate, threshold);
    }

    /**
     * Get top performing trades (winners) for a date
     */
    public List<PnlExplain> getTopWinners(LocalDate pnlDate, int limit) {
        List<PnlExplain> allPnl = pnlExplainRepository.findByPnlDate(pnlDate);
        return allPnl.stream()
            .filter(pnl -> pnl.getTotalPnl() != null)
            .sorted((a, b) -> b.getTotalPnl().compareTo(a.getTotalPnl()))
            .limit(limit)
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Get worst performing trades (losers) for a date
     */
    public List<PnlExplain> getTopLosers(LocalDate pnlDate, int limit) {
        List<PnlExplain> allPnl = pnlExplainRepository.findByPnlDate(pnlDate);
        return allPnl.stream()
            .filter(pnl -> pnl.getTotalPnl() != null)
            .sorted((a, b) -> a.getTotalPnl().compareTo(b.getTotalPnl()))
            .limit(limit)
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Get realized P&L total for a date (from settled trades)
     */
    public BigDecimal getRealizedPnl(LocalDate pnlDate) {
        List<PnlExplain> pnlExplains = pnlExplainRepository.findByPnlDate(pnlDate);
        return pnlExplains.stream()
            .filter(pnl -> pnl.getTrade() != null && pnl.getTrade().getStatus() == com.trading.ctrm.trade.TradeStatus.SETTLED)
            .map(PnlExplain::getTotalPnl)
            .filter(java.util.Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Get unrealized P&L total for a date (from open positions)
     */
    public BigDecimal getUnrealizedPnl(LocalDate pnlDate) {
        List<PnlExplain> pnlExplains = pnlExplainRepository.findByPnlDate(pnlDate);
        return pnlExplains.stream()
            .filter(pnl -> pnl.getTrade() != null && pnl.getTrade().getStatus() != com.trading.ctrm.trade.TradeStatus.SETTLED)
            .filter(pnl -> pnl.getTrade().getStatus() != com.trading.ctrm.trade.TradeStatus.CANCELLED)
            .filter(pnl -> pnl.getTrade().getStatus() != com.trading.ctrm.trade.TradeStatus.REJECTED)
            .map(PnlExplain::getTotalPnl)
            .filter(java.util.Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
