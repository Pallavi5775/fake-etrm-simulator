package com.trading.ctrm.trade;

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
@RequestMapping("/api/trades")
public class TradeCsvUploadController {
    private static final Logger log = LoggerFactory.getLogger(TradeCsvUploadController.class);
    @Autowired
    private TradeService tradeService;

    @PostMapping("/upload-csv")
    public ResponseEntity<List<Trade>> uploadCsv(@RequestParam("file") MultipartFile file) {
        List<Trade> createdTrades = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean header = true;
            while ((line = reader.readLine()) != null) {
                if (header) { header = false; continue; }
                String[] fields = line.split(",");
                // TODO: Map fields to Trade entity. Adjust indices as per CSV format.
                Trade trade = new Trade();
                // trade.set... (set fields)
                createdTrades.add(tradeService.saveTrade(trade));
            }
        } catch (Exception e) {
            log.error("CSV upload failed", e);
            throw new RuntimeException("CSV upload failed", e);
        }
        return ResponseEntity.ok(createdTrades);
    }
}
