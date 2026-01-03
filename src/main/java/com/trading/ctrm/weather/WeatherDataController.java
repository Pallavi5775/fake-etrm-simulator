package com.trading.ctrm.weather;

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
@RequestMapping("/api/weather-data")
public class WeatherDataController {
    @Autowired
    private WeatherDataRepository repository;

    @GetMapping
    public List<WeatherData> listAll() {
        return repository.findAll();
    }

    @PostMapping("/upload-csv")
    public ResponseEntity<?> uploadCsv(@RequestParam("file") MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            List<WeatherData> batch = new ArrayList<>();
            reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                WeatherData wd = new WeatherData();
                wd.setLocation(parts[0]);
                wd.setDate(LocalDate.parse(parts[1]));
                wd.setTemperature(Double.valueOf(parts[2]));
                wd.setPrecipitation(Double.valueOf(parts[3]));
                batch.add(wd);
            }
            repository.saveAll(batch);
            return ResponseEntity.ok("Uploaded " + batch.size() + " records");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<WeatherData> getById(@PathVariable Long id) {
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
