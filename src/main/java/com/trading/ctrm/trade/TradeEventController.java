package com.trading.ctrm.trade;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trading.ctrm.lifestyle.TradeLifecycleEngine;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/trades")
public class TradeEventController {

    private final TradeLifecycleEngine lifecycleEngine;
    private final TradeEventRepository tradeEventRepository;

    public TradeEventController(TradeLifecycleEngine lifecycleEngine, TradeEventRepository tradeEventRepository) {
        this.lifecycleEngine = lifecycleEngine;
        this.tradeEventRepository = tradeEventRepository;
    }


   @PostMapping("/{tradeId}/events/{event}")
@Transactional
public ResponseEntity<?> handleEvent(
        @PathVariable Long tradeId,
        @PathVariable String event,
        @RequestBody(required = false) Map<String, Object> payload
) {
    lifecycleEngine.handleEvent(tradeId, event, payload);
    return ResponseEntity.ok().build();
}

   @GetMapping("/trades/{id}/timeline")
public List<TradeEvent> getTimeline(@PathVariable Long id) {
    return tradeEventRepository.findByTradeIdOrderByCreatedAt(id);
}

}
