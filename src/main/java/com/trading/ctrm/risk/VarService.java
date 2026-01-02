package com.trading.ctrm.risk;

import com.trading.ctrm.trade.Trade;
import com.trading.ctrm.trade.TradeRepository;
import com.trading.ctrm.pricing.ValuationResult;
import com.trading.ctrm.pricing.ValuationResultRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * VaR Service - Simplified Value at Risk calculation
 * Parametric VaR using delta-normal approach
 */
@Service
public class VarService {

    private static final Logger log = LoggerFactory.getLogger(VarService.class);

    private final TradeRepository tradeRepository;
    private final ValuationResultRepository valuationResultRepository;
    private final PositionService positionService;

    public VarService(
            TradeRepository tradeRepository,
            ValuationResultRepository valuationResultRepository,
            PositionService positionService) {
        this.tradeRepository = tradeRepository;
        this.valuationResultRepository = valuationResultRepository;
        this.positionService = positionService;
    }

    /**
     * Calculate portfolio VaR using delta-normal method
     * VaR = Delta * Spot * Volatility * Z-score * sqrt(holding period)
     */
    public BigDecimal calculateVaR(String portfolio, LocalDate valueDate, double confidenceLevel, int holdingPeriodDays) {
        log.info("Calculating VaR for portfolio: {} at {}% confidence", portfolio, confidenceLevel * 100);

        // Get portfolio positions
        List<Position> positions = positionService.getPortfolioPositions(portfolio, valueDate);

        if (positions.isEmpty()) {
            log.warn("No positions found for portfolio: {}", portfolio);
            return BigDecimal.ZERO;
        }

        // Get Z-score for confidence level
        double zScore = getZScore(confidenceLevel);

        // Calculate VaR for each position
        BigDecimal totalVaR = BigDecimal.ZERO;

        for (Position position : positions) {
            BigDecimal positionVaR = calculatePositionVaR(position, zScore, holdingPeriodDays);
            totalVaR = totalVaR.add(positionVaR);
        }

        log.info("Portfolio VaR: {}", totalVaR);
        return totalVaR;
    }

    /**
     * Calculate VaR for a single position
     */
    private BigDecimal calculatePositionVaR(Position position, double zScore, int holdingPeriodDays) {
        // Simplified VaR calculation
        // VaR = |Delta| * Spot * Volatility * Z-score * sqrt(holding period)

        BigDecimal delta = position.getDelta().abs();
        
        // Assume spot price from position MTM (simplified)
        BigDecimal spotPrice = position.getNetMtm().abs();
        
        // Assume volatility (simplified - would come from vol surface)
        double volatility = 0.30; // 30% annual volatility
        
        // Time adjustment
        double timeAdjustment = Math.sqrt(holdingPeriodDays / 252.0); // 252 trading days per year
        
        // Calculate VaR
        BigDecimal var = delta
            .multiply(spotPrice)
            .multiply(BigDecimal.valueOf(volatility))
            .multiply(BigDecimal.valueOf(zScore))
            .multiply(BigDecimal.valueOf(timeAdjustment));

        log.debug("Position VaR for {}: Delta={}, Spot={}, VaR={}", 
            position.getCommodity(), delta, spotPrice, var);

        return var;
    }

    /**
     * Get Z-score for confidence level
     */
    private double getZScore(double confidenceLevel) {
        // Standard Z-scores for common confidence levels
        if (confidenceLevel >= 0.99) return 2.33;  // 99%
        if (confidenceLevel >= 0.975) return 1.96; // 97.5%
        if (confidenceLevel >= 0.95) return 1.645; // 95%
        if (confidenceLevel >= 0.90) return 1.28;  // 90%
        return 1.645; // Default to 95%
    }

    /**
     * Calculate historical VaR using historical simulation
     * (Simplified - would need historical price data)
     */
    public BigDecimal calculateHistoricalVaR(String portfolio, LocalDate valueDate, double confidenceLevel, int lookbackDays) {
        log.info("Calculating Historical VaR for portfolio: {}", portfolio);
        
        // Simplified - would need to:
        // 1. Get historical prices for lookback period
        // 2. Calculate historical returns
        // 3. Apply returns to current positions
        // 4. Sort P&L distribution
        // 5. Find VaR at confidence level percentile
        
        // For now, return parametric VaR
        return calculateVaR(portfolio, valueDate, confidenceLevel, 1);
    }

    /**
     * Calculate Conditional VaR (Expected Shortfall)
     * CVaR = Expected loss beyond VaR
     */
    public BigDecimal calculateCVaR(String portfolio, LocalDate valueDate, double confidenceLevel) {
        log.info("Calculating CVaR for portfolio: {}", portfolio);
        
        // Simplified CVaR calculation
        // CVaR = VaR * (1 + adjustment factor)
        BigDecimal var = calculateVaR(portfolio, valueDate, confidenceLevel, 1);
        
        // Adjustment factor based on confidence level
        double adjustmentFactor = confidenceLevel >= 0.99 ? 1.15 : 1.10;
        
        BigDecimal cvar = var.multiply(BigDecimal.valueOf(adjustmentFactor));
        
        log.info("Portfolio CVaR: {}", cvar);
        return cvar;
    }

    /**
     * Calculate marginal VaR for a trade
     * Shows VaR contribution of a single trade
     */
    public BigDecimal calculateMarginalVaR(Long tradeId, LocalDate valueDate, double confidenceLevel) {
        log.info("Calculating marginal VaR for trade: {}", tradeId);
        
        Trade trade = tradeRepository.findById(tradeId)
            .orElseThrow(() -> new RuntimeException("Trade not found: " + tradeId));
        
        String portfolio = trade.getPortfolio();
        
        // Calculate VaR with trade
        BigDecimal varWith = calculateVaR(portfolio, valueDate, confidenceLevel, 1);
        
        // Would need to calculate VaR without trade (simplified)
        // For now, approximate as proportional to position delta
        
        return varWith.multiply(BigDecimal.valueOf(0.1)); // Simplified
    }
}
