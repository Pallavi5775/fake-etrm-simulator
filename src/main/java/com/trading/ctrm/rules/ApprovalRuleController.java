package com.trading.ctrm.rules;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.trading.ctrm.rules.dto.ApprovalRuleDto;

@RestController
@RequestMapping("/api/approval-rules")
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

    /**
     * Upload approval rules from CSV file
     * POST /api/approval-rules/upload-csv
     * 
     * CSV Format: ruleName,triggerEvent,priority,active,status,version,conditionField,conditionOperator,conditionValue,approvalRole,approvalLevel
     * Multiple conditions can be separated by semicolons in conditionField/conditionOperator/conditionValue
     * Multiple routing levels should be on separate rows with same ruleName
     */
    @PostMapping("/upload-csv")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> uploadRulesFromCsv(@RequestParam("file") MultipartFile file) {
        List<ApprovalRuleDto> created = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        Map<String, ApprovalRule> rulesByName = new HashMap<>();
        int lineNumber = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            String[] headers = null;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                
                if (lineNumber == 1) {
                    headers = line.split(",");
                    continue; // Skip header
                }

                try {
                    String[] values = line.split(",", -1);
                    Map<String, String> row = new HashMap<>();
                    
                    for (int i = 0; i < headers.length && i < values.length; i++) {
                        row.put(headers[i].trim(), values[i].trim());
                    }

                    String ruleName = row.get("ruleName");
                    if (ruleName == null || ruleName.isEmpty()) {
                        errors.add("Line " + lineNumber + ": Rule name is required");
                        continue;
                    }

                    // Get or create rule
                    ApprovalRule rule = rulesByName.get(ruleName);
                    if (rule == null) {
                        rule = new ApprovalRule();
                        rule.setRuleName(ruleName);
                        rule.setTriggerEvent(row.getOrDefault("triggerEvent", "TRADE_BOOK"));
                        
                        String priority = row.get("priority");
                        rule.setPriority(priority != null && !priority.isEmpty() ? Integer.parseInt(priority) : 1);
                        
                        String active = row.get("active");
                        rule.setActive("true".equalsIgnoreCase(active) || "1".equals(active));
                        
                        rule.setStatus(row.getOrDefault("status", "ACTIVE"));
                        
                        String version = row.get("version");
                        rule.setVersion(version != null && !version.isEmpty() ? Integer.parseInt(version) : 1);
                        
                        rule.setConditions(new ArrayList<>());
                        rule.setRouting(new ArrayList<>());
                        
                        rulesByName.put(ruleName, rule);
                    }

                    // Parse conditions (semicolon-separated for multiple conditions on same row)
                    String conditionField = row.get("conditionField");
                    String conditionOperator = row.get("conditionOperator");
                    String conditionValue = row.get("conditionValue");
                    
                    if (conditionField != null && !conditionField.isEmpty()) {
                        String[] fields = conditionField.split(";");
                        String[] operators = conditionOperator != null ? conditionOperator.split(";") : new String[]{};
                        String[] conditionValues = conditionValue != null ? conditionValue.split(";") : new String[]{};
                        
                        for (int i = 0; i < fields.length; i++) {
                            // Only add condition if not already added
                            String field = fields[i].trim();
                            String operator = i < operators.length ? operators[i].trim() : ">";
                            String value = i < conditionValues.length ? conditionValues[i].trim() : "";
                            
                            boolean conditionExists = rule.getConditions().stream()
                                .anyMatch(c -> c.getFieldCode().equals(field) && 
                                              c.getOperator().equals(operator) && 
                                              c.getValue1().equals(value));
                            
                            if (!conditionExists && !value.isEmpty()) {
                                ApprovalRuleCondition condition = new ApprovalRuleCondition();
                                condition.setFieldCode(field);
                                condition.setOperator(operator);
                                condition.setValue1(value);
                                condition.setRule(rule);
                                rule.getConditions().add(condition);
                            }
                        }
                    }

                    // Parse routing
                    String approvalRole = row.get("approvalRole");
                    String approvalLevelStr = row.get("approvalLevel");
                    
                    if (approvalRole != null && !approvalRole.isEmpty() && approvalLevelStr != null && !approvalLevelStr.isEmpty()) {
                        int approvalLevel = Integer.parseInt(approvalLevelStr);
                        
                        // Check if this routing level already exists
                        boolean routingExists = rule.getRouting().stream()
                            .anyMatch(r -> r.getApprovalRole().equals(approvalRole) && 
                                          r.getApprovalLevel() == approvalLevel);
                        
                        if (!routingExists) {
                            ApprovalRouting routing = new ApprovalRouting();
                            routing.setApprovalRole(approvalRole);
                            routing.setApprovalLevel(approvalLevel);
                            routing.setRule(rule);
                            rule.getRouting().add(routing);
                        }
                    }

                } catch (Exception e) {
                    errors.add("Line " + lineNumber + ": " + e.getMessage());
                }
            }

            // Save all rules
            for (ApprovalRule rule : rulesByName.values()) {
                try {
                    if (rule.getConditions().isEmpty()) {
                        errors.add("Rule '" + rule.getRuleName() + "': No conditions defined");
                        continue;
                    }
                    if (rule.getRouting().isEmpty()) {
                        errors.add("Rule '" + rule.getRuleName() + "': No routing defined");
                        continue;
                    }
                    
                    ApprovalRule saved = repo.save(rule);
                    created.add(ApprovalRuleDto.from(saved));
                } catch (Exception e) {
                    errors.add("Rule '" + rule.getRuleName() + "': " + e.getMessage());
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse CSV file: " + e.getMessage(), e);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("created", created.size());
        response.put("errors", errors.size());
        response.put("errorDetails", errors);
        response.put("rules", created);
        
        return response;
    }

    // Metadata endpoints for condition dropdowns (Endur-style)
    @GetMapping("/metadata/fields")
    public List<String> getConditionFields() {
        return List.of(
            "trade.quantity",
            "trade.price",
            "trade.counterparty",
            "trade.portfolio",
            "trade.status",
            "trade.currency",
            "trade.instrumentType"
        );
    }

    @GetMapping("/metadata/operators")
    public List<String> getConditionOperators() {
        return List.of(
            ">",
            "<",
            "=",
            "!=",
            ">=",
            "<="
        );
    }

    @GetMapping("/metadata/values")
    public List<String> getConditionValues(@RequestParam String field) {
        switch (field) {
            case "trade.counterparty":
                return List.of("BP", "SHELL", "TOTAL", "CHEVRON", "EXXON");
            case "trade.portfolio":
                return List.of("PORT1", "PORT2", "PORT3", "PORT4");
            case "trade.status":
                return List.of("CREATED", "VALIDATED", "BOOKED", "APPROVED", "SETTLED");
            case "trade.currency":
                return List.of("USD", "EUR", "GBP", "JPY");
            case "trade.instrumentType":
                return List.of("GAS_FORWARD", "POWER_FORWARD", "COMMODITY_SWAP", "OPTION", "RENEWABLE_PPA");
            default:
                // For numeric fields like quantity, price
                return List.of("0", "100", "1000", "10000", "100000");
        }
    }
}

