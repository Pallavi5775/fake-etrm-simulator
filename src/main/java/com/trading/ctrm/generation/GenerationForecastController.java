package com.trading.ctrm.generation;

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
@RequestMapping("/api/generation-forecasts")
public class GenerationForecastController {
    @Autowired
    private GenerationForecastRepository repository;

    @GetMapping
    public List<GenerationForecast> listAll() {
        return repository.findAll();
    }

    @PostMapping("/upload-csv")
    public ResponseEntity<?> uploadCsv(@RequestParam("file") MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            List<GenerationForecast> batch = new ArrayList<>();
            reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                GenerationForecast gf = new GenerationForecast();
                gf.setPlantName(parts[0]);
                gf.setDate(LocalDate.parse(parts[1]));
                gf.setForecastMWh(Double.valueOf(parts[2]));
                batch.add(gf);
            }
            repository.saveAll(batch);
            return ResponseEntity.ok("Uploaded " + batch.size() + " records");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenerationForecast> getById(@PathVariable Long id) {
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
