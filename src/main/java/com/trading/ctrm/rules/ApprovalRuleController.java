package com.trading.ctrm.rules;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trading.ctrm.rules.dto.ApprovalRuleDto;

@RestController
@RequestMapping("/api/approval-rules")
@CrossOrigin(origins = "*")
public class ApprovalRuleController {

    private final ApprovalRuleRepository repo;
    private final ApprovalRuleVersioningService versioningService;

    public ApprovalRuleController(
        ApprovalRuleRepository repo,
        ApprovalRuleVersioningService versioningService
    ) {
        this.repo = repo;
        this.versioningService = versioningService;
    }

    @PostMapping
    public ApprovalRuleDto create(@RequestBody ApprovalRule rule) {
        rule.getConditions().forEach(c -> c.setRule(rule));
        rule.getRouting().forEach(r -> r.setRule(rule));
        ApprovalRule saved = repo.save(rule);
        return ApprovalRuleDto.from(saved);
    }

    @GetMapping
    public List<ApprovalRuleDto> getAll() {
        return repo.findAll().stream()
            .map(ApprovalRuleDto::from)
            .collect(Collectors.toList());
    }

    @PostMapping("/{id}/activate")
    public ApprovalRuleDto activate(@PathVariable Long id) {
        ApprovalRule rule = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Rule not found"));
        rule.setStatus("ACTIVE");
        rule.setActive(true);
        ApprovalRule saved = repo.save(rule);
        return ApprovalRuleDto.from(saved);
    }

    @PostMapping("/{id}/new-version")
    public ApprovalRuleDto createNewVersion(@PathVariable Long id) {
        ApprovalRule newVersion = versioningService.createNewVersion(id);
        return ApprovalRuleDto.from(newVersion);
    }

    @DeleteMapping("/{id}")
    public void deleteRule(@PathVariable Long id) {
        ApprovalRule rule = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Rule not found with id: " + id));
        
        // Check if rule is in use (has pending approvals referencing it)
        // You can add additional validation here if needed
        
        repo.delete(rule);
    }
}

