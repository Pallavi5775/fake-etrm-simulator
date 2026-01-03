package com.trading.ctrm.yieldcurve;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/yield-curves")
public class YieldCurveController {
    @Autowired
    private YieldCurveRepository repository;

    @GetMapping
    public List<YieldCurve> listAll() {
        return repository.findAll();
    }

    @PostMapping("/upload-csv")
    public ResponseEntity<?> uploadCsv(@RequestParam("file") MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            List<YieldCurve> batch = new ArrayList<>();
            reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                YieldCurve yc = new YieldCurve();
                yc.setCurveName(parts[0]);
                yc.setDate(LocalDate.parse(parts[1]));
                yc.setYield(Double.valueOf(parts[2]));
                batch.add(yc);
            }
            repository.saveAll(batch);
            return ResponseEntity.ok("Uploaded " + batch.size() + " records");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<YieldCurve> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) return ResponseEntity.notFound().build();
        repository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
