package com.trading.ctrm.marketdata;

import com.trading.ctrm.instrument.Instrument;
import com.trading.ctrm.trade.InstrumentRepository;
import com.trading.ctrm.trade.ForwardCurve;
import com.trading.ctrm.trade.ForwardCurveRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/forward-curves")
public class ForwardCurveController {

    private final ForwardCurveRepository curveRepository;
    private final InstrumentRepository instrumentRepository;

    public ForwardCurveController(
            ForwardCurveRepository curveRepository,
            InstrumentRepository instrumentRepository) {
        this.curveRepository = curveRepository;
        this.instrumentRepository = instrumentRepository;
    }

    /**
     * Create or update a single forward curve point
     * POST /api/forward-curves
     * Body: {"instrumentCode": "PWR-Q1-25", "deliveryDate": "2026-01-02", "price": 60.50}
     */
    @PostMapping
    public ResponseEntity<ForwardCurveResponse> createOrUpdateCurvePoint(
            @RequestBody ForwardCurveRequest request) {
        
        Instrument instrument = instrumentRepository
                .findOptionalByInstrumentCode(request.getInstrumentCode())
                .orElseThrow(() -> new RuntimeException(
                        "Instrument not found: " + request.getInstrumentCode()));

        ForwardCurve curve = curveRepository
                .findByInstrumentAndDeliveryDate(instrument, request.getDeliveryDate())
                .orElse(new ForwardCurve());

        curve.setInstrument(instrument);
        curve.setDeliveryDate(request.getDeliveryDate());
        curve.setPrice(request.getPrice());
        curve.setCurveDate(LocalDate.now());

        ForwardCurve saved = curveRepository.save(curve);
        return ResponseEntity.ok(toResponse(saved));
    }

    /**
     * Bulk upload forward curve points
     * POST /api/forward-curves/bulk
     * Body: [
     *   {"instrumentCode": "PWR-Q1-25", "deliveryDate": "2026-01-02", "price": 60.50},
     *   {"instrumentCode": "PWR-Q1-25", "deliveryDate": "2026-01-03", "price": 61.00}
     * ]
     */
    @PostMapping("/bulk")
    public ResponseEntity<BulkUploadResponse> bulkUpload(
            @RequestBody List<ForwardCurveRequest> requests) {
        
        int created = 0;
        int updated = 0;
        int errors = 0;

        for (ForwardCurveRequest request : requests) {
            try {
                Instrument instrument = instrumentRepository
                        .findOptionalByInstrumentCode(request.getInstrumentCode())
                        .orElseThrow(() -> new RuntimeException(
                                "Instrument not found: " + request.getInstrumentCode()));

                boolean exists = curveRepository
                        .findByInstrumentAndDeliveryDate(instrument, request.getDeliveryDate())
                        .isPresent();

                ForwardCurve curve = curveRepository
                        .findByInstrumentAndDeliveryDate(instrument, request.getDeliveryDate())
                        .orElse(new ForwardCurve());

                curve.setInstrument(instrument);
                curve.setDeliveryDate(request.getDeliveryDate());
                curve.setPrice(request.getPrice());
                curve.setCurveDate(LocalDate.now());

                curveRepository.save(curve);

                if (exists) {
                    updated++;
                } else {
                    created++;
                }

            } catch (Exception e) {
                errors++;
            }
        }

        BulkUploadResponse response = new BulkUploadResponse(
                requests.size(), created, updated, errors);
        return ResponseEntity.ok(response);
    }

    /**
     * Get forward curve for a specific instrument and date
     * GET /api/forward-curves?instrumentCode=PWR-Q1-25&deliveryDate=2026-01-02
     */
    @GetMapping
    public ResponseEntity<ForwardCurveResponse> getCurvePoint(
            @RequestParam String instrumentCode,
            @RequestParam LocalDate deliveryDate) {
        
        Instrument instrument = instrumentRepository
                .findOptionalByInstrumentCode(instrumentCode)
                .orElseThrow(() -> new RuntimeException(
                        "Instrument not found: " + instrumentCode));

        ForwardCurve curve = curveRepository
                .findByInstrumentAndDeliveryDate(instrument, deliveryDate)
                .orElseThrow(() -> new RuntimeException(
                        "Forward curve not found for " + instrumentCode 
                        + " on " + deliveryDate));

        return ResponseEntity.ok(toResponse(curve));
    }

    /**
     * Get all forward curve points for an instrument
     * GET /api/forward-curves/instrument/PWR-Q1-25
     */
    @GetMapping("/instrument/{instrumentCode}")
    public ResponseEntity<List<ForwardCurveResponse>> getCurvesByInstrument(
            @PathVariable String instrumentCode) {
        
        Instrument instrument = instrumentRepository
                .findOptionalByInstrumentCode(instrumentCode)
                .orElseThrow(() -> new RuntimeException(
                        "Instrument not found: " + instrumentCode));

        List<ForwardCurve> curves = curveRepository
                .findByInstrumentOrderByDeliveryDate(instrument);

        List<ForwardCurveResponse> responses = curves.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    /**
     * Delete a forward curve point
     * DELETE /api/forward-curves?instrumentCode=PWR-Q1-25&deliveryDate=2026-01-02
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteCurvePoint(
            @RequestParam String instrumentCode,
            @RequestParam LocalDate deliveryDate) {
        
        Instrument instrument = instrumentRepository
                .findOptionalByInstrumentCode(instrumentCode)
                .orElseThrow(() -> new RuntimeException(
                        "Instrument not found: " + instrumentCode));

        ForwardCurve curve = curveRepository
                .findByInstrumentAndDeliveryDate(instrument, deliveryDate)
                .orElseThrow(() -> new RuntimeException(
                        "Forward curve not found for " + instrumentCode 
                        + " on " + deliveryDate));

        curveRepository.delete(curve);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get all instruments that have forward curves
     * GET /api/forward-curves/instruments
     */
    @GetMapping("/instruments")
    public ResponseEntity<List<String>> getInstrumentsWithCurves() {
        List<String> instrumentCodes = curveRepository.findDistinctInstruments();
        return ResponseEntity.ok(instrumentCodes);
    }

    private ForwardCurveResponse toResponse(ForwardCurve curve) {
        return new ForwardCurveResponse(
                curve.getId(),
                curve.getInstrument().getInstrumentCode(),
                curve.getDeliveryDate(),
                curve.getPrice(),
                curve.getCurveDate()
        );
    }
}
