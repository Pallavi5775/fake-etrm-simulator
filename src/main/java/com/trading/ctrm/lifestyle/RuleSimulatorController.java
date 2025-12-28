package com.trading.ctrm.lifestyle;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/simulator")
public class RuleSimulatorController {

    private final RuleSimulatorService simulatorService;

    public RuleSimulatorController(RuleSimulatorService simulatorService) {
        this.simulatorService = simulatorService;
    }

    @PostMapping("/run")
    public SimulationResult simulate(@RequestBody SimulationRequest req) {
        return simulatorService.simulate(req);
    }
}

