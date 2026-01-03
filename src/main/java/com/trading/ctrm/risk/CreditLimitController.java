package com.trading.ctrm.risk;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/credit-limits")
public class CreditLimitController {

    private final RiskLimitRepository riskLimitRepository;

    @Autowired
    public CreditLimitController(RiskLimitRepository riskLimitRepository) {
        this.riskLimitRepository = riskLimitRepository;
    }

    // GET /api/credit-limits
    @GetMapping
    public List<RiskLimit> getAllCreditLimits() {
        return riskLimitRepository.findByLimitType("CREDIT");
    }

    // POST /api/credit-limits
    @PostMapping
    public RiskLimit createCreditLimit(@RequestBody RiskLimit limit) {
        limit.setLimitType("CREDIT");
        return riskLimitRepository.save(limit);
    }

    // PUT /api/credit-limits/{id}
    @PutMapping("/{id}")
    public ResponseEntity<RiskLimit> updateCreditLimit(@PathVariable Long id, @RequestBody RiskLimit limit) {
        return riskLimitRepository.findById(id)
                .map(existing -> {
                    limit.setLimitId(id);
                    limit.setLimitType("CREDIT");
                    return ResponseEntity.ok(riskLimitRepository.save(limit));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/credit-limits/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCreditLimit(@PathVariable Long id) {
        if (riskLimitRepository.existsById(id)) {
            riskLimitRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}