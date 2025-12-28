package com.trading.ctrm.lifestyle;

import java.util.List;

import org.springframework.stereotype.Service;

import com.trading.ctrm.trade.Trade;
import com.trading.ctrm.trade.TradeRepository;

@Service
public class RuleSimulatorService {

    private final TradeRepository tradeRepo;
    private final LifecycleRuleRepository ruleRepo;
    private final RuleEngine engine;

    public RuleSimulatorService(
            TradeRepository tradeRepo,
            LifecycleRuleRepository ruleRepo,
            RuleEngine engine
    ) {
        this.tradeRepo = tradeRepo;
        this.ruleRepo = ruleRepo;
        this.engine = engine;
    }

    public SimulationResult simulate(SimulationRequest req) {

        Trade trade = tradeRepo.findByTradeId(req.getTradeId())
                .orElseThrow(() ->
                    new IllegalArgumentException("Trade not found: " + req.getTradeId())
                );

        ExecutionContext ctx = new ExecutionContext();
        ctx.setTrade(trade);
        ctx.setMode(ExecutionMode.SIMULATOR);

        if (req.getOverrideAttributes() != null) {
            ctx.getAttributes().putAll(req.getOverrideAttributes());
        }

        List<Long> ruleIds = req.getRuleIds();

        if (ruleIds == null || ruleIds.isEmpty()) {
            throw new IllegalArgumentException("At least one ruleId must be provided");
        }

        List<LifecycleRule> rules = ruleRepo.findAllById(ruleIds);
        List<RuleExecutionTrace> traces =
                engine.simulate(rules, ctx);   // ✅ CORRECT METHOD

        SimulationResult result = new SimulationResult();
        result.setTradeId(trade.getTradeId());
        result.setFinalStatus(trade.getStatus().name());
        result.setTraces(traces);

        return result;
    }
}
