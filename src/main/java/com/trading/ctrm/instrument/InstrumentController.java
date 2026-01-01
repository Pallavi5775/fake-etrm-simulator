package com.trading.ctrm.instrument;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.trading.ctrm.trade.InstrumentRepository;

import java.util.List;

@RestController
@RequestMapping("/api/instruments")
@CrossOrigin(origins = "*")
public class InstrumentController {

    private final InstrumentRepository instrumentRepository;

    public InstrumentController(InstrumentRepository instrumentRepository) {
        this.instrumentRepository = instrumentRepository;
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
        instrument.setCommodity(request.getCommodity());
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
        instrument.setCommodity(request.getCommodity());
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
}
