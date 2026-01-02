package com.trading.ctrm.risk;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Risk Limit Service - Endur-style risk limit monitoring
 * Checks positions and risk metrics against defined limits
 */
@Service
public class RiskLimitService {

    private static final Logger log = LoggerFactory.getLogger(RiskLimitService.class);

    private final RiskLimitRepository limitRepository;
    private final RiskLimitBreachRepository breachRepository;
    private final PositionService positionService;

    public RiskLimitService(
            RiskLimitRepository limitRepository,
            RiskLimitBreachRepository breachRepository,
            PositionService positionService) {
        this.limitRepository = limitRepository;
        this.breachRepository = breachRepository;
        this.positionService = positionService;
    }

    /**
     * Check all limits for a portfolio
     */
    @Transactional
    public List<RiskLimitBreach> checkLimits(String portfolio, LocalDate checkDate) {
        log.info("Checking risk limits for portfolio: {}", portfolio);

        List<RiskLimit> limits = limitRepository.findByActiveTrue();
        
        for (RiskLimit limit : limits) {
            if (limit.getLimitScope().equals("PORTFOLIO") && limit.getScopeValue().equals(portfolio)) {
                checkLimit(limit, portfolio, checkDate);
            }
        }

        return breachRepository.findByBreachStatus("ACTIVE");
    }

    /**
     * Check single limit
     */
    @Transactional
    public void checkLimit(RiskLimit limit, String portfolio, LocalDate checkDate) {
        log.debug("Checking limit: {} for {}", limit.getLimitName(), portfolio);

        BigDecimal currentValue = getCurrentValue(limit, portfolio, checkDate);
        BigDecimal limitValue = limit.getLimitValue();
        BigDecimal warningThreshold = limit.getWarningThreshold();

        log.debug("Current value: {}, Limit: {}, Warning: {}", currentValue, limitValue, warningThreshold);

        // Check for breach
        if (currentValue.abs().compareTo(limitValue) > 0) {
            createBreach(limit, currentValue, limitValue, "BREACH");
        } else if (warningThreshold != null && currentValue.abs().compareTo(warningThreshold) > 0) {
            createBreach(limit, currentValue, limitValue, "WARNING");
        }
    }

    /**
     * Get current value for limit check
     */
    private BigDecimal getCurrentValue(RiskLimit limit, String portfolio, LocalDate checkDate) {
        switch (limit.getLimitType()) {
            case "POSITION":
                return positionService.getPortfolioNetPosition(portfolio, checkDate);
            
            case "VAR":
                // Simplified - would call VaR service
                return BigDecimal.ZERO;
            
            case "DELTA":
                // Get total delta from positions
                List<Position> positions = positionService.getPortfolioPositions(portfolio, checkDate);
                return positions.stream()
                    .map(Position::getDelta)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            default:
                return BigDecimal.ZERO;
        }
    }

    /**
     * Create limit breach record
     */
    private void createBreach(RiskLimit limit, BigDecimal currentValue, BigDecimal limitValue, String severity) {
        log.warn("Limit breach detected: {} - {} - Current: {}, Limit: {}", 
            limit.getLimitName(), severity, currentValue, limitValue);

        RiskLimitBreach breach = new RiskLimitBreach();
        breach.setLimitId(limit.getLimitId());
        breach.setCurrentValue(currentValue);
        breach.setLimitValue(limitValue);
        breach.setBreachAmount(currentValue.subtract(limitValue));
        
        if (limitValue.compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal breachPct = currentValue.subtract(limitValue)
                .divide(limitValue, 6, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
            breach.setBreachPercent(breachPct);
        }
        
        breach.setSeverity(severity);
        breach.setBreachStatus("ACTIVE");

        breachRepository.save(breach);

        // Take action based on breach action
        if ("BLOCK".equals(limit.getBreachAction())) {
            log.error("BLOCKING ACTION: Limit breach requires blocking further trades");
            // Would integrate with approval system to block trades
        }
    }

    /**
     * Resolve breach
     */
    @Transactional
    public void resolveBreach(Long breachId, String resolvedBy, String notes) {
        Optional<RiskLimitBreach> breachOpt = breachRepository.findById(breachId);
        
        if (breachOpt.isPresent()) {
            RiskLimitBreach breach = breachOpt.get();
            breach.setBreachStatus("RESOLVED");
            breach.setResolvedBy(resolvedBy);
            breach.setResolvedAt(LocalDateTime.now());
            breach.setResolutionNotes(notes);
            breachRepository.save(breach);
            
            log.info("Breach {} resolved by {}", breachId, resolvedBy);
        }
    }

    /**
     * Get active breaches
     */
    public List<RiskLimitBreach> getActiveBreaches() {
        return breachRepository.findByBreachStatus("ACTIVE");
    }

    /**
     * Get critical breaches
     */
    public List<RiskLimitBreach> getCriticalBreaches() {
        return breachRepository.findActiveBreachesBySeverity("BREACH");
    }

    /**
     * Get recent breaches
     */
    public List<RiskLimitBreach> getRecentBreaches(int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        return breachRepository.findRecentBreaches(startDate);
    }

    /**
     * Create or update limit
     */
    @Transactional
    public RiskLimit saveLimit(RiskLimit limit) {
        if (limit.getLimitId() == null) {
            limit.setCreatedAt(LocalDateTime.now());
        }
        limit.setLastModifiedAt(LocalDateTime.now());
        return limitRepository.save(limit);
    }

    /**
     * Get all active limits
     */
    public List<RiskLimit> getActiveLimits() {
        return limitRepository.findByActiveTrue();
    }
}
