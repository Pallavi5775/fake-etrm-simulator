package com.trading.ctrm.deals;

import com.trading.ctrm.instrument.Instrument;
import com.trading.ctrm.trade.InstrumentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/templates")
public class DealTemplateController {

    private final DealTemplateRepository templateRepository;
    private final InstrumentRepository instrumentRepository;

    public DealTemplateController(
            DealTemplateRepository templateRepository,
            InstrumentRepository instrumentRepository) {
        this.templateRepository = templateRepository;
        this.instrumentRepository = instrumentRepository;
    }

    /**
     * Get all deal templates
     */
    @GetMapping
    public List<DealTemplateDto> getAllTemplates() {
        return templateRepository.findAll()
                .stream()
                .map(DealTemplateDto::from)
                .collect(Collectors.toList());
    }

    /**
     * Get template by ID
     */
    @GetMapping("/{id}")
    public DealTemplateDto getTemplateById(@PathVariable Long id) {
        DealTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Template not found with id: " + id));
        return DealTemplateDto.from(template);
    }

    /**
     * Get template by name
     */
    @GetMapping("/by-name/{name}")
    public DealTemplateDto getTemplateByName(@PathVariable String name) {
        DealTemplate template = templateRepository.findByTemplateName(name);
        if (template == null) {
            throw new IllegalArgumentException("Template not found with name: " + name);
        }
        return DealTemplateDto.from(template);
    }

    /**
     * Create a new deal template
     * POST /api/templates
     */
    // @PreAuthorize("hasAnyRole('SENIOR_TRADER', 'HEAD_TRADER', 'ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DealTemplateDto createTemplate(@RequestBody DealTemplateRequest request) {
        
        // Validate required fields
        if (request.getTemplateName() == null || request.getTemplateName().isBlank()) {
            throw new IllegalArgumentException("Template name is required");
        }
        if (request.getInstrumentId() == null) {
            throw new IllegalArgumentException("Instrument ID is required");
        }
        if (request.getDefaultPrice() == null) {
            throw new IllegalArgumentException("Default price is required");
        }

        // Fetch the instrument
        Instrument instrument = instrumentRepository.findById(request.getInstrumentId())
                .orElseThrow(() -> new IllegalArgumentException("Instrument not found with id: " + request.getInstrumentId()));

        DealTemplate template = new DealTemplate();
        template.setTemplateName(request.getTemplateName());
        template.setInstrument(instrument);
        template.setDefaultQuantity(request.getDefaultQuantity());
        template.setDefaultPrice(request.getDefaultPrice());
        template.setAutoApprovalAllowed(request.isAutoApprovalAllowed());
        
        // Set optional overrides - if null, will inherit from instrument
        template.setCommodity(request.getCommodity());
        template.setInstrumentType(request.getInstrumentType());
        template.setUnit(request.getUnit());
        template.setCurrency(request.getCurrency());
        template.setMtmApprovalThreshold(request.getMtmApprovalThreshold());
        template.setPricingModel(request.getPricingModel());

        DealTemplate saved = templateRepository.save(template);
        return DealTemplateDto.from(saved);
    }

    /**
     * Enable auto-approval for a template
     */
    // @PreAuthorize("hasAnyRole('RISK', 'ADMIN')")
    @PostMapping("/{id}/enable-auto-approval")
    public DealTemplateDto enableAutoApproval(@PathVariable Long id) {
        DealTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Template not found with id: " + id));
        
        template.setAutoApprovalAllowed(true);
        DealTemplate saved = templateRepository.save(template);
        return DealTemplateDto.from(saved);
    }

    /**
     * Disable auto-approval for a template
     */
    // @PreAuthorize("hasAnyRole('RISK', 'ADMIN')")
    @PostMapping("/{id}/disable-auto-approval")
    public DealTemplateDto disableAutoApproval(@PathVariable Long id) {
        DealTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Template not found with id: " + id));
        
        template.setAutoApprovalAllowed(false);
        DealTemplate saved = templateRepository.save(template);
        return DealTemplateDto.from(saved);
    }

    /**
     * Toggle auto-approval for a template
     */
    // @PreAuthorize("hasAnyRole('RISK', 'ADMIN')")
    @PatchMapping("/{id}/auto-approval")
    public DealTemplateDto toggleAutoApproval(
            @PathVariable Long id,
            @RequestParam boolean enabled
    ) {
        DealTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Template not found with id: " + id));
        
        template.setAutoApprovalAllowed(enabled);
        DealTemplate saved = templateRepository.save(template);
        return DealTemplateDto.from(saved);
    }

    /**
     * Upload deal templates from CSV file
     * POST /api/templates/upload-csv
     * 
     * CSV Format: templateName,instrumentCode,defaultQuantity,defaultPrice,autoApprovalAllowed,mtmApprovalThreshold,commodity,unit,currency
     */
    @PostMapping("/upload-csv")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> uploadTemplatesFromCsv(@RequestParam("file") MultipartFile file) {
        List<DealTemplateDto> created = new ArrayList<>();
        List<String> errors = new ArrayList<>();
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
                    String[] values = line.split(",", -1); // -1 to keep trailing empty strings
                    Map<String, String> row = new HashMap<>();
                    
                    for (int i = 0; i < headers.length && i < values.length; i++) {
                        row.put(headers[i].trim(), values[i].trim());
                    }

                    String templateName = row.get("templateName");
                    String instrumentCode = row.get("instrumentCode");

                    // Validate required fields
                    if (templateName == null || templateName.isEmpty()) {
                        errors.add("Line " + lineNumber + ": Template name is required");
                        continue;
                    }

                    if (instrumentCode == null || instrumentCode.isEmpty()) {
                        errors.add("Line " + lineNumber + ": Instrument code is required");
                        continue;
                    }

                    // Find instrument
                    Instrument instrument = instrumentRepository.findByInstrumentCode(instrumentCode);
                    if (instrument == null) {
                        errors.add("Line " + lineNumber + ": Instrument not found: " + instrumentCode);
                        continue;
                    }

                    // Check if template already exists
                    if (templateRepository.findByTemplateName(templateName) != null) {
                        errors.add("Line " + lineNumber + ": Template already exists: " + templateName);
                        continue;
                    }

                    // Create template
                    DealTemplate template = new DealTemplate();
                    template.setTemplateName(templateName);
                    template.setInstrument(instrument);
                    
                    // Set default values
                    String defaultQuantity = row.get("defaultQuantity");
                    if (defaultQuantity != null && !defaultQuantity.isEmpty()) {
                        template.setDefaultQuantity(new BigDecimal(defaultQuantity));
                    }
                    
                    String defaultPrice = row.get("defaultPrice");
                    if (defaultPrice != null && !defaultPrice.isEmpty()) {
                        template.setDefaultPrice(new BigDecimal(defaultPrice));
                    }
                    
                    String autoApproval = row.get("autoApprovalAllowed");
                    template.setAutoApprovalAllowed("true".equalsIgnoreCase(autoApproval) || "1".equals(autoApproval));
                    
                    // Set optional overrides
                    String mtmThreshold = row.get("mtmApprovalThreshold");
                    if (mtmThreshold != null && !mtmThreshold.isEmpty()) {
                        template.setMtmApprovalThreshold(new BigDecimal(mtmThreshold));
                    }
                    
                    String commodity = row.get("commodity");
                    if (commodity != null && !commodity.isEmpty()) {
                        template.setCommodity(commodity);
                    }
                    
                    String unit = row.get("unit");
                    if (unit != null && !unit.isEmpty()) {
                        template.setUnit(unit);
                    }
                    
                    String currency = row.get("currency");
                    if (currency != null && !currency.isEmpty()) {
                        template.setCurrency(currency);
                    }

                    DealTemplate saved = templateRepository.save(template);
                    created.add(DealTemplateDto.from(saved));

                } catch (Exception e) {
                    errors.add("Line " + lineNumber + ": " + e.getMessage());
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse CSV file: " + e.getMessage(), e);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("created", created.size());
        response.put("errors", errors.size());
        response.put("errorDetails", errors);
        response.put("templates", created);
        
        return response;
    }
}
