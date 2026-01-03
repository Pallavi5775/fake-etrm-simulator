package com.trading.ctrm.risk;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/credit-limits")
public class CreditLimitCsvUploadController {
    private static final Logger log = LoggerFactory.getLogger(CreditLimitCsvUploadController.class);
    @Autowired
    private RiskLimitService riskLimitService;

    @PostMapping("/upload-csv")
    public ResponseEntity<List<RiskLimit>> uploadCsv(@RequestParam("file") MultipartFile file) {
        List<RiskLimit> createdLimits = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean header = true;
            while ((line = reader.readLine()) != null) {
                if (header) { header = false; continue; }
                String[] fields = line.split(",");
                // TODO: Map fields to RiskLimit entity. Adjust indices as per CSV format.
                RiskLimit limit = new RiskLimit();
                // limit.set... (set fields)
                createdLimits.add(riskLimitService.saveLimit(limit));
            }
        } catch (Exception e) {
            log.error("CSV upload failed", e);
            throw new RuntimeException("CSV upload failed", e);
        }
        return ResponseEntity.ok(createdLimits);
    }
}
