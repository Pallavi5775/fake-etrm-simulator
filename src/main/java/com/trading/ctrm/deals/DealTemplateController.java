package com.trading.ctrm.deals;

import com.trading.ctrm.instrument.Instrument;
import com.trading.ctrm.trade.InstrumentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/templates")
@CrossOrigin(origins = "*")
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

        DealTemplate saved = templateRepository.save(template);
        return DealTemplateDto.from(saved);
    }

    /**
     * Enable auto-approval for a template
     */
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
}
