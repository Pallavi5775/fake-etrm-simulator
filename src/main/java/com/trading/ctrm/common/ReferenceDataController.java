package com.trading.ctrm.common;

import org.springframework.web.bind.annotation.*;
import com.trading.ctrm.instrument.InstrumentType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ReferenceDataController {

    private final CounterpartyRepository counterpartyRepository;
    private final PortfolioRepository portfolioRepository;

    public ReferenceDataController(
            CounterpartyRepository counterpartyRepository,
            PortfolioRepository portfolioRepository) {
        this.counterpartyRepository = counterpartyRepository;
        this.portfolioRepository = portfolioRepository;
    }

    // GET /api/counterparties
    @GetMapping("/counterparties")
    public List<Counterparty> getAllCounterparties() {
        return counterpartyRepository.findAll();
    }

    // GET /api/portfolios  
    @GetMapping("/portfolios")
    public List<Portfolio> getAllPortfolios() {
        return portfolioRepository.findAll();
    }

    // POST /api/counterparties
    // @PreAuthorize("hasAnyRole('RISK', 'ADMIN')")
    @PostMapping("/counterparties")
    public Counterparty createCounterparty(@RequestBody Counterparty counterparty) {
        return counterpartyRepository.save(counterparty);
    }

    // PUT /api/counterparties/{id}
    // @PreAuthorize("hasAnyRole('RISK', 'ADMIN')")
    @PutMapping("/counterparties/{id}")
    public Counterparty updateCounterparty(@PathVariable Long id, @RequestBody Counterparty counterparty) {
        return counterpartyRepository.findById(id)
                .map(existing -> {
                    existing.setName(counterparty.getName());
                    existing.setCreditRating(counterparty.getCreditRating());
                    existing.setCountry(counterparty.getCountry());
                    existing.setActive(counterparty.getActive());
                    return counterpartyRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Counterparty not found: " + id));
    }

    // DELETE /api/counterparties/{id}
    // @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/counterparties/{id}")
    public void deleteCounterparty(@PathVariable Long id) {
        counterpartyRepository.deleteById(id);
    }

    // POST /api/portfolios
    // @PreAuthorize("hasAnyRole('RISK', 'ADMIN')")
    @PostMapping("/portfolios")
    public Portfolio createPortfolio(@RequestBody Portfolio portfolio) {
        return portfolioRepository.save(portfolio);
    }

    // PUT /api/portfolios/{id}
    // @PreAuthorize("hasAnyRole('RISK', 'ADMIN')")
    @PutMapping("/portfolios/{id}")
    public Portfolio updatePortfolio(@PathVariable Long id, @RequestBody Portfolio portfolio) {
        return portfolioRepository.findById(id)
                .map(existing -> {
                    existing.setName(portfolio.getName());
                    existing.setDescription(portfolio.getDescription());
                    existing.setRiskOwner(portfolio.getRiskOwner());
                    existing.setActive(portfolio.getActive());
                    return portfolioRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Portfolio not found: " + id));
    }

    // DELETE /api/portfolios/{id}
    // @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/portfolios/{id}")
    public void deletePortfolio(@PathVariable Long id) {
        portfolioRepository.deleteById(id);
    }

    // ===== REFERENCE DATA ENDPOINTS =====

    /**
     * GET /api/reference-data/instrument-types
     * Returns all instrument types for dropdowns
     */
    @GetMapping("/reference-data/instrument-types")
    public List<ReferenceDataItem> getInstrumentTypes() {
        return Arrays.stream(InstrumentType.values())
                .map(type -> new ReferenceDataItem(
                    type.name(),
                    formatDisplayName(type.name())
                ))
                .collect(Collectors.toList());
    }

    /**
     * GET /api/reference-data/commodities
     * Returns all commodities for dropdowns
     */
    @GetMapping("/reference-data/commodities")
    public List<ReferenceDataItem> getCommodities() {
        return Arrays.asList(
            new ReferenceDataItem("POWER", "Power"),
            new ReferenceDataItem("NATURAL_GAS", "Natural Gas"),
            new ReferenceDataItem("OIL", "Oil"),
            new ReferenceDataItem("COAL", "Coal"),
            new ReferenceDataItem("CARBON", "Carbon"),
            new ReferenceDataItem("LNG", "Liquefied Natural Gas")
        );
    }

    /**
     * GET /api/reference-data/currencies
     * Returns all currencies for dropdowns
     */
    @GetMapping("/reference-data/currencies")
    public List<ReferenceDataItem> getCurrencies() {
        return Arrays.asList(
            new ReferenceDataItem("EUR", "Euro"),
            new ReferenceDataItem("USD", "US Dollar"),
            new ReferenceDataItem("GBP", "British Pound"),
            new ReferenceDataItem("JPY", "Japanese Yen"),
            new ReferenceDataItem("CHF", "Swiss Franc"),
            new ReferenceDataItem("CAD", "Canadian Dollar")
        );
    }

    /**
     * GET /api/reference-data/units
     * Returns all units for dropdowns
     */
    @GetMapping("/reference-data/units")
    public List<ReferenceDataItem> getUnits() {
        return Arrays.asList(
            new ReferenceDataItem("MWh", "Megawatt Hour"),
            new ReferenceDataItem("GWh", "Gigawatt Hour"),
            new ReferenceDataItem("MMBtu", "Million British Thermal Units"),
            new ReferenceDataItem("Therm", "Therm"),
            new ReferenceDataItem("BBL", "Barrel"),
            new ReferenceDataItem("MT", "Metric Ton"),
            new ReferenceDataItem("tCO2e", "Tons CO2 Equivalent")
        );
    }

    /**
     * Helper method to format enum names for display
     */
    private String formatDisplayName(String enumName) {
        return Arrays.stream(enumName.split("_"))
                .map(word -> word.charAt(0) + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }
}
