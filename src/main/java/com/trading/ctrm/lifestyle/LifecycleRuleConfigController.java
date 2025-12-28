package com.trading.ctrm.lifestyle;

import com.trading.ctrm.lifestyle.dto.LifecycleRuleRequest;
import com.trading.ctrm.lifestyle.dto.LifecycleRuleResponse;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/config/lifecycle-rules")
@CrossOrigin
public class LifecycleRuleConfigController {

    private final JpaLifecycleRuleRepository repository;

    public LifecycleRuleConfigController(JpaLifecycleRuleRepository repository) {
        this.repository = repository;
    }

@GetMapping
public List<LifecycleRuleResponse> getAll() {

    return repository.findAll()
            .stream()
            .map(LifecycleRuleMapper::toResponse)
            .toList();
}

    // 2️⃣ Create a new rule (CONFIG CREATION)
   @PostMapping
    public LifecycleRule create(
            @Valid @RequestBody LifecycleRuleRequest req
    ) {
        if (req == null) {
            throw new IllegalArgumentException("LifecycleRuleRequest must not be null");
        }

        LifecycleRule rule = LifecycleRuleBuilder.fromRequest(req);
        return repository.save(rule);
    }
    // 3️⃣ Enable / Disable rule (CONFIG TOGGLE)
    @PutMapping("/{id}/toggle")
    public LifecycleRule toggle(@PathVariable long  id) {

        LifecycleRule rule = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rule not found: " + id));

        rule.toggleEnabled();
        return repository.save(rule);
    }
}
