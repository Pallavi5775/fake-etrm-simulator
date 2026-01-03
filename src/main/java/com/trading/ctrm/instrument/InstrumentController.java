package com.trading.ctrm.instrument;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.trading.ctrm.trade.InstrumentRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/instruments")
public class InstrumentController {

    private final InstrumentRepository instrumentRepository;
    private final InstrumentEntityHelper instrumentEntityHelper;

    @Autowired
    public InstrumentController(InstrumentRepository instrumentRepository, InstrumentEntityHelper instrumentEntityHelper) {
        this.instrumentRepository = instrumentRepository;
        this.instrumentEntityHelper = instrumentEntityHelper;
    }

    /**
     * Get all instruments
     * GET /api/instruments
     */
    @GetMapping
    public List<Instrument> getAllInstruments() {
        return instrumentRepository.findAll();
    }

    /**
     * Get instrument by code
     * GET /api/instruments/{code}
     */
    @GetMapping("/{code}")
    public Instrument getInstrumentByCode(@PathVariable String code) {
        Instrument instrument = instrumentRepository.findByInstrumentCode(code);
        if (instrument == null) {
            throw new IllegalArgumentException("Instrument not found: " + code);
        }
        return instrument;
    }

    /**
     * Create a new Power Forward instrument
     * POST /api/instruments/power-forward
     */
    @PostMapping("/power-forward")
    @ResponseStatus(HttpStatus.CREATED)
    public PowerForwardInstrument createPowerForward(@RequestBody PowerForwardRequest request) {
        
        // Validate instrument code doesn't already exist
        if (instrumentRepository.existsByInstrumentCode(request.getInstrumentCode())) {
            throw new IllegalArgumentException("Instrument code already exists: " + request.getInstrumentCode());
        }

        PowerForwardInstrument instrument = new PowerForwardInstrument();
        instrument.setInstrumentCode(request.getInstrumentCode());
        instrument.setCommodity(request.getCommodity() != null ? request.getCommodity() : "POWER");
        instrument.setCurrency(request.getCurrency() != null ? request.getCurrency() : "EUR");
        instrument.setUnit(request.getUnit() != null ? request.getUnit() : "MWh");
        instrument.setStartDate(request.getStartDate());
        instrument.setEndDate(request.getEndDate());

        return instrumentRepository.save(instrument);
    }

    /**
     * Create a new Gas Forward instrument
     * POST /api/instruments/gas-forward
     */
    @PostMapping("/gas-forward")
    @ResponseStatus(HttpStatus.CREATED)
    public GasForwardInstrument createGasForward(@RequestBody GasForwardRequest request) {
        
        if (instrumentRepository.existsByInstrumentCode(request.getInstrumentCode())) {
            throw new IllegalArgumentException("Instrument code already exists: " + request.getInstrumentCode());
        }

        GasForwardInstrument instrument = new GasForwardInstrument();
        instrument.setInstrumentCode(request.getInstrumentCode());
        instrument.setCommodity(request.getCommodity() != null ? request.getCommodity() : "GAS");
        instrument.setCurrency(request.getCurrency() != null ? request.getCurrency() : "EUR");
        instrument.setUnit(request.getUnit() != null ? request.getUnit() : "MMBtu");
        instrument.setDeliveryDate(request.getDeliveryDate());

        return instrumentRepository.save(instrument);
    }

    /**
     * Create a new Renewable PPA instrument
     * POST /api/instruments/renewable-ppa
     */
    @PostMapping("/renewable-ppa")
    @ResponseStatus(HttpStatus.CREATED)
    public RenewablePPAInstrument createRenewablePPA(@RequestBody RenewablePPARequest request) {
        
        if (instrumentRepository.existsByInstrumentCode(request.getInstrumentCode())) {
            throw new IllegalArgumentException("Instrument code already exists: " + request.getInstrumentCode());
        }

        RenewablePPAInstrument instrument = new RenewablePPAInstrument();
        instrument.setInstrumentCode(request.getInstrumentCode());
        instrument.setCommodity(request.getCommodity() != null ? request.getCommodity() : "POWER");
        instrument.setCurrency(request.getCurrency() != null ? request.getCurrency() : "EUR");
        instrument.setUnit(request.getUnit() != null ? request.getUnit() : "MWh");
        instrument.setTechnology(request.getTechnology());
        instrument.setForecastCurve(request.getForecastCurve());
        instrument.setSettlementType(request.getSettlementType());

        return instrumentRepository.save(instrument);
    }

    /**
     * Create a new Commodity Swap instrument
     * POST /api/instruments/commodity-swap
     */
    @PostMapping("/commodity-swap")
    @ResponseStatus(HttpStatus.CREATED)
    public CommoditySwapInstrument createCommoditySwap(@RequestBody CommoditySwapRequest request) {
        
        if (instrumentRepository.existsByInstrumentCode(request.getInstrumentCode())) {
            throw new IllegalArgumentException("Instrument code already exists: " + request.getInstrumentCode());
        }

        CommoditySwapInstrument instrument = new CommoditySwapInstrument();
        instrument.setInstrumentCode(request.getInstrumentCode());
        instrument.setCommodity(instrumentEntityHelper.resolveCommodity(request.getCommodityId()));
        instrument.setCurrency(request.getCurrency() != null ? request.getCurrency() : "EUR");
        instrument.setUnit(request.getUnit());

        return instrumentRepository.save(instrument);
    }

    /**
     * Create a new Commodity Option instrument
     * POST /api/instruments/commodity-option
     */
    @PostMapping("/commodity-option")
    @ResponseStatus(HttpStatus.CREATED)
    public CommodityOptionInstrument createCommodityOption(@RequestBody CommodityOptionRequest request) {
        
        if (instrumentRepository.existsByInstrumentCode(request.getInstrumentCode())) {
            throw new IllegalArgumentException("Instrument code already exists: " + request.getInstrumentCode());
        }

        CommodityOptionInstrument instrument = new CommodityOptionInstrument();
        instrument.setInstrumentCode(request.getInstrumentCode());
        instrument.setCommodity(instrumentEntityHelper.resolveCommodity(request.getCommodityId()));
        instrument.setCurrency(request.getCurrency() != null ? request.getCurrency() : "EUR");
        instrument.setUnit(request.getUnit());
        instrument.setStrikePrice(request.getStrikePrice());
        instrument.setExpiryDate(request.getExpiryDate());
        instrument.setOptionType(request.getOptionType());

        return instrumentRepository.save(instrument);
    }

    /**
     * Delete an instrument by code
     * DELETE /api/instruments/{code}
     */
    @DeleteMapping("/{code}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInstrument(@PathVariable String code) {
        Instrument instrument = instrumentRepository.findByInstrumentCode(code);
        if (instrument == null) {
            throw new IllegalArgumentException("Instrument not found: " + code);
        }
        instrumentRepository.delete(instrument);
    }

    /**
     * Upload instruments from CSV file
     * POST /api/instruments/upload-csv
     * 
     * CSV Format: instrumentType,instrumentCode,commodity,currency,unit,startDate,endDate,deliveryDate,strikePrice,expiryDate,optionType
     */
    @PostMapping("/upload-csv")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> uploadInstrumentsFromCsv(@RequestParam("file") MultipartFile file) {
        List<Instrument> created = new ArrayList<>();
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

                    String instrumentType = row.get("instrumentType");
                    String instrumentCode = row.get("instrumentCode");

                    // Check if instrument already exists
                    if (instrumentRepository.existsByInstrumentCode(instrumentCode)) {
                        errors.add("Line " + lineNumber + ": Instrument code already exists: " + instrumentCode);
                        continue;
                    }

                    Instrument instrument = null;

                    switch (instrumentType.toUpperCase()) {
                        case "POWER_FORWARD":
                            PowerForwardInstrument powerForward = new PowerForwardInstrument();
                            powerForward.setInstrumentCode(instrumentCode);
                            powerForward.setCommodity(row.getOrDefault("commodity", "POWER"));
                            powerForward.setCurrency(row.getOrDefault("currency", "EUR"));
                            powerForward.setUnit(row.getOrDefault("unit", "MWh"));
                            if (!row.get("startDate").isEmpty()) {
                                powerForward.setStartDate(LocalDate.parse(row.get("startDate")));
                            }
                            if (!row.get("endDate").isEmpty()) {
                                powerForward.setEndDate(LocalDate.parse(row.get("endDate")));
                            }
                            instrument = powerForward;
                            break;

                        case "GAS_FORWARD":
                            GasForwardInstrument gasForward = new GasForwardInstrument();
                            gasForward.setInstrumentCode(instrumentCode);
                            gasForward.setCommodity(row.getOrDefault("commodity", "NATURAL_GAS"));
                            gasForward.setCurrency(row.getOrDefault("currency", "EUR"));
                            gasForward.setUnit(row.getOrDefault("unit", "MMBtu"));
                            if (!row.get("deliveryDate").isEmpty()) {
                                gasForward.setDeliveryDate(LocalDate.parse(row.get("deliveryDate")));
                            }
                            instrument = gasForward;
                            break;

                        case "COMMODITY_OPTION":
                            CommodityOptionInstrument option = new CommodityOptionInstrument();
                            option.setInstrumentCode(instrumentCode);
                            option.setCommodity(row.getOrDefault("commodity", "POWER"));
                            option.setCurrency(row.getOrDefault("currency", "EUR"));
                            option.setUnit(row.getOrDefault("unit", "MWh"));
                            if (!row.get("strikePrice").isEmpty()) {
                                option.setStrikePrice(new BigDecimal(row.get("strikePrice")));
                            }
                            if (!row.get("expiryDate").isEmpty()) {
                                option.setExpiryDate(LocalDate.parse(row.get("expiryDate")));
                            }
                            option.setOptionType(row.getOrDefault("optionType", "CALL"));
                            instrument = option;
                            break;

                        default:
                            errors.add("Line " + lineNumber + ": Unsupported instrument type: " + instrumentType);
                            continue;
                    }

                    if (instrument != null) {
                        Instrument saved = instrumentRepository.save(instrument);
                        created.add(saved);
                    }

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
        response.put("instruments", created);
        
        return response;
    }
}
