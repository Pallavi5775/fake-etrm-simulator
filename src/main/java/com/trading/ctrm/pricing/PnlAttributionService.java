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

        // Get all active trades (APPROVED or BOOKED status)
        List<Trade> approvedTrades = tradeRepository.findByStatus(com.trading.ctrm.trade.TradeStatus.APPROVED);
        List<Trade> bookedTrades = tradeRepository.findByStatus(com.trading.ctrm.trade.TradeStatus.BOOKED);
        
        log.info("Found {} approved trades and {} booked trades", approvedTrades.size(), bookedTrades.size());
        
        // Combine both lists
        List<Trade> trades = new java.util.ArrayList<>(approvedTrades);
        trades.addAll(bookedTrades);
        
        log.info("Total trades to calculate P&L for: {}", trades.size());

        for (Trade trade : trades) {
            try {
                log.info("Calculating P&L for trade: {}", trade.getTradeId());
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

        try {
            // Calculate MTM using forward curve pricing
            BigDecimal mtmT1 = pricingService.calculateMTM(trade, pnlDate);
            BigDecimal mtmT0 = pricingService.calculateMTM(trade, previousDate);
            
            BigDecimal totalPnl = mtmT1.subtract(mtmT0);

            // Create P&L explain
            PnlExplain pnl = new PnlExplain();
            pnl.setTradeId(trade.getId());
            pnl.setPnlDate(pnlDate);
            pnl.setTotalPnl(totalPnl);
            pnl.setPnlSpotMove(totalPnl); // All P&L attributed to spot move for now
            pnl.setUnexplained(BigDecimal.ZERO);

            // Save P&L explain
            pnlExplainRepository.save(pnl);

            log.debug("Trade {} P&L: {}", trade.getTradeId(), totalPnl);
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
}
