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

    public PnlAttributionService(
            TradeRepository tradeRepository,
            ValuationResultRepository valuationResultRepository,
            PnlExplainRepository pnlExplainRepository) {
        this.tradeRepository = tradeRepository;
        this.valuationResultRepository = valuationResultRepository;
        this.pnlExplainRepository = pnlExplainRepository;
    }

    /**
     * Calculate daily P&L for all trades
     */
    @Transactional
    public void calculateDailyPnl(LocalDate pnlDate) {
        log.info("Calculating P&L for date: {}", pnlDate);

        LocalDate previousDate = pnlDate.minusDays(1);

        // Get all active trades
        List<Trade> trades = tradeRepository.findByStatus(com.trading.ctrm.trade.TradeStatus.BOOKED);

        for (Trade trade : trades) {
            try {
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
        log.debug("Calculating P&L for trade: {}", trade.getTradeId());

        // Get valuations for T0 and T1
        Optional<ValuationResult> valT0Opt = valuationResultRepository
            .findByTradeIdAndPricingDate(trade.getId(), previousDate);
        Optional<ValuationResult> valT1Opt = valuationResultRepository
            .findByTradeIdAndPricingDate(trade.getId(), pnlDate);

        if (valT0Opt.isEmpty() || valT1Opt.isEmpty()) {
            log.warn("Missing valuations for trade {}: T0={}, T1={}", 
                trade.getTradeId(), valT0Opt.isPresent(), valT1Opt.isPresent());
            return null;
        }

        ValuationResult valT0 = valT0Opt.get();
        ValuationResult valT1 = valT1Opt.get();

        // Create P&L explain
        PnlExplain pnl = new PnlExplain();
        pnl.setTradeId(trade.getId());
        pnl.setPnlDate(pnlDate);
        pnl.setValuationT0(valT0.getResultId());
        pnl.setValuationT1(valT1.getResultId());

        // Calculate total P&L
        BigDecimal totalPnl = valT1.getMtmTotal().subtract(valT0.getMtmTotal());
        pnl.setTotalPnl(totalPnl);

        // Attribution by component
        // 1. Spot move (delta * spot change)
        BigDecimal spotPnl = calculateSpotPnl(valT0, valT1);
        pnl.setPnlSpotMove(spotPnl);

        // 2. Forward curve move
        BigDecimal curvePnl = calculateCurvePnl(valT0, valT1);
        pnl.setPnlCurveMove(curvePnl);

        // 3. Volatility move (vega * vol change)
        BigDecimal volPnl = calculateVolPnl(valT0, valT1);
        pnl.setPnlVolMove(volPnl);

        // 4. Time decay (theta)
        BigDecimal thetaPnl = valT0.getTheta() != null ? valT0.getTheta() : BigDecimal.ZERO;
        pnl.setPnlTimeDecay(thetaPnl);

        // 5. Carry (interest/dividend accrual)
        BigDecimal carryPnl = BigDecimal.ZERO; // Simplified
        pnl.setPnlCarry(carryPnl);

        // 6. Calculate unexplained
        BigDecimal explained = spotPnl.add(curvePnl).add(volPnl).add(thetaPnl).add(carryPnl);
        BigDecimal unexplained = totalPnl.subtract(explained);
        pnl.setUnexplained(unexplained);

        log.debug("Trade {} P&L: Total={}, Spot={}, Curve={}, Vol={}, Theta={}, Unexplained={}",
            trade.getTradeId(), totalPnl, spotPnl, curvePnl, volPnl, thetaPnl, unexplained);

        return pnlExplainRepository.save(pnl);
    }

    /**
     * Calculate spot P&L (delta * spot change)
     */
    private BigDecimal calculateSpotPnl(ValuationResult valT0, ValuationResult valT1) {
        if (valT0.getMtmSpot() == null || valT1.getMtmSpot() == null) {
            return BigDecimal.ZERO;
        }
        return valT1.getMtmSpot().subtract(valT0.getMtmSpot());
    }

    /**
     * Calculate forward curve P&L
     */
    private BigDecimal calculateCurvePnl(ValuationResult valT0, ValuationResult valT1) {
        if (valT0.getMtmForward() == null || valT1.getMtmForward() == null) {
            return BigDecimal.ZERO;
        }
        return valT1.getMtmForward().subtract(valT0.getMtmForward());
    }

    /**
     * Calculate volatility P&L (vega * vol change)
     */
    private BigDecimal calculateVolPnl(ValuationResult valT0, ValuationResult valT1) {
        // Simplified: use vega if available
        if (valT0.getVega() == null) {
            return BigDecimal.ZERO;
        }
        // In real implementation, would calculate implied vol change
        return BigDecimal.ZERO;
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
}
