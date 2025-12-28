package com.trading.ctrm.lifestyle;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trading.ctrm.trade.TradeEventType;
import com.trading.ctrm.trade.TradeStatus;

@RestController
@RequestMapping("/api/simulator/run")
@CrossOrigin(origins = "*")
public class LifecycleRuleSimulatorController {

    private final TradeLifecycleEngine engine;

    public LifecycleRuleSimulatorController(TradeLifecycleEngine engine) {
        this.engine = engine;
    }

    @PostMapping("/lifecycle")
    public SimulationResult simulate(@RequestBody SimulationRequest req) {

        try {
            TradeEventType eventType =
                    TradeEventType.valueOf(req.getEventType());

            TradeStatus next = engine.resolveNextStatus(
                    req.getFromStatus(),
                    eventType,
                    req.getDesk()
            );

            return SimulationResult.allowed(next);

        } catch (Exception ex) {
            return SimulationResult.rejected(ex.getMessage());
        }
    }
}
