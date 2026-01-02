package com.trading.ctrm.pricing;

import com.trading.ctrm.trade.Trade;
import com.trading.ctrm.trade.TradeRepository;
import com.trading.ctrm.instrument.Instrument;
import com.trading.ctrm.trade.InstrumentRepository;
import com.trading.ctrm.rules.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

/**
 * Scenario Service - Endur-style stress testing and what-if analysis
 */
@Service
public class ScenarioService {

    private static final Logger log = LoggerFactory.getLogger(ScenarioService.class);

    private final TradeRepository tradeRepository;
    private final InstrumentRepository instrumentRepository;
    private final ValuationScenarioRepository scenarioRepository;
    private final ScenarioResultRepository scenarioResultRepository;
    private final ValuationResultRepository valuationResultRepository;
    private final PricingEngineFactory pricingEngineFactory;

    public ScenarioService(
            TradeRepository tradeRepository,
            InstrumentRepository instrumentRepository,
            ValuationScenarioRepository scenarioRepository,
            ScenarioResultRepository scenarioResultRepository,
            ValuationResultRepository valuationResultRepository,
            PricingEngineFactory pricingEngineFactory) {
        this.tradeRepository = tradeRepository;
        this.instrumentRepository = instrumentRepository;
        this.scenarioRepository = scenarioRepository;
        this.scenarioResultRepository = scenarioResultRepository;
        this.valuationResultRepository = valuationResultRepository;
        this.pricingEngineFactory = pricingEngineFactory;
    }

    /**
     * Run scenario analysis for all trades
     */
    @Transactional
    public ValuationScenario runScenario(String scenarioName, String scenarioType, 
                                        LocalDate baseDate, String parameters, 
                                        String portfolioFilter, String createdBy) {
        log.info("Running scenario: {} of type: {}", scenarioName, scenarioType);

        // Create scenario
        ValuationScenario scenario = new ValuationScenario(scenarioName, scenarioType, baseDate);
        scenario.setDescription("Scenario analysis: " + scenarioType);
        scenario.setParameters(parameters);
        scenario.setCreatedBy(createdBy);
        scenario = scenarioRepository.save(scenario);

        // Get trades
        List<Trade> trades = getTradesToAnalyze(portfolioFilter);
        log.info("Analyzing {} trades", trades.size());

        // Run scenario for each trade
        for (Trade trade : trades) {
            try {
                analyzeTradeScenario(trade, scenario, scenarioType, parameters);
            } catch (Exception e) {
                log.error("Failed to analyze trade: {}", trade.getTradeId(), e);
            }
        }

        return scenario;
    }

    /**
     * Analyze single trade under scenario
     */
    private void analyzeTradeScenario(Trade trade, ValuationScenario scenario, 
                                     String scenarioType, String parameters) {
        log.debug("Analyzing trade {} under scenario {}", trade.getTradeId(), scenario.getScenarioId());

        // Get instrument from trade
        Instrument instrument = trade.getInstrument();

        // Get base case valuation
        ValuationResult baseValuation = valuationResultRepository
            .findByTradeIdAndPricingDate(trade.getId(), scenario.getBaseDate())
            .orElse(null);

        BigDecimal baseMtm = (baseValuation != null) ? baseValuation.getMtmTotal() : BigDecimal.ZERO;

        // Build scenario context
        ValuationContext scenarioContext = buildScenarioContext(trade, scenarioType, parameters);

        // Calculate scenario valuation
        PricingEngine engine = pricingEngineFactory.getEngine(instrument.getInstrumentType());
        ValuationResult scenarioValuation = engine.price(trade, instrument, scenarioContext);

        BigDecimal scenarioMtm = scenarioValuation.getMtmTotal();

        // Calculate impact
        BigDecimal impact = scenarioMtm.subtract(baseMtm);
        BigDecimal impactPct = baseMtm.compareTo(BigDecimal.ZERO) != 0 
            ? impact.divide(baseMtm, 6, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
            : BigDecimal.ZERO;

        // Save result
        ScenarioResult result = new ScenarioResult();
        result.setScenarioId(scenario.getScenarioId());
        result.setTradeId(trade.getId());
        result.setBaseMtm(baseMtm);
        result.setScenarioMtm(scenarioMtm);
        result.setPnlImpact(impact);
        result.setPnlImpactPct(impactPct);

        scenarioResultRepository.save(result);

        log.debug("Trade {} impact: {} ({}%)", trade.getTradeId(), impact, impactPct);
    }

    /**
     * Build scenario context with shocked parameters
     */
    private ValuationContext buildScenarioContext(Trade trade, String scenarioType, String parameters) {
        // Default context
        ValuationContext.Builder builder = ValuationContext.builder()
            .trade(TradeContext.fromTrade(trade))
            .accounting(AccountingContext.fromTrade(trade))
            .credit(CreditContext.fromTrade(trade))
            .audit(AuditContext.fromTrade(trade));

        // Apply scenario shocks
        switch (scenarioType) {
            case "SPOT_SHOCK":
                // Shock spot price by X%
                double spotShock = parseShockParameter(parameters, "spotShock", 10.0);
                builder.market(MarketContext.fromTrade(trade))
                       .pricing(PricingContext.fromTrade(trade));
                break;

            case "CURVE_SHIFT":
                // Parallel shift of forward curve
                double curveShift = parseShockParameter(parameters, "curveShift", 5.0);
                builder.market(MarketContext.fromTrade(trade))
                      .pricing(PricingContext.fromTrade(trade));
                break;

            case "VOL_SHOCK":
                // Shock implied volatility
                double volShock = parseShockParameter(parameters, "volShock", 20.0);
                builder.market(MarketContext.fromTrade(trade))
                      .pricing(PricingContext.fromTrade(trade));
                break;

            default:
                // Default scenario
                builder.market(MarketContext.fromTrade(trade))
                      .pricing(PricingContext.fromTrade(trade));
        }

        builder.risk(RiskContext.fromTrade(trade));

        return builder.build();
    }

    /**
     * Parse shock parameter from JSON string
     */
    private double parseShockParameter(String parameters, String key, double defaultValue) {
        // Simplified parsing - in real implementation use JSON parser
        if (parameters == null || parameters.isEmpty()) {
            return defaultValue;
        }
        // For now, just return default
        return defaultValue;
    }

    /**
     * Get trades to analyze
     */
    private List<Trade> getTradesToAnalyze(String portfolioFilter) {
        if (portfolioFilter == null || portfolioFilter.isBlank() || portfolioFilter.equals("ALL")) {
            return tradeRepository.findByStatus(com.trading.ctrm.trade.TradeStatus.BOOKED);
        } else {
            return tradeRepository.findByPortfolioAndStatus(portfolioFilter, com.trading.ctrm.trade.TradeStatus.BOOKED);
        }
    }

    /**
     * Get scenario results
     */
    public List<ScenarioResult> getScenarioResults(Long scenarioId) {
        return scenarioResultRepository.findByScenarioId(scenarioId);
    }

    /**
     * Get total impact for scenario
     */
    public BigDecimal getTotalImpact(Long scenarioId) {
        BigDecimal total = scenarioResultRepository.getTotalImpactForScenario(scenarioId);
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Get top impacts for scenario
     */
    public List<ScenarioResult> getTopImpacts(Long scenarioId) {
        return scenarioResultRepository.findTopImpactsByScenario(scenarioId);
    }
}
