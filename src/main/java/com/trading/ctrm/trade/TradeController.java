package com.trading.ctrm.trade;

import com.trading.ctrm.trade.dto.TradeEventRequest;
import com.trading.ctrm.trade.dto.TradeResponseDto;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trades")
@CrossOrigin
public class TradeController {

    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    // ===============================
    // 1️⃣ BOOK TRADE (Front Office)
    // ===============================
    @PostMapping
    public TradeResponseDto bookTrade(
            @RequestBody TradeEventRequest req) {

        Trade trade = tradeService.bookTrade(req);
        return toDto(trade);
    }

    // ======================================
    // 2️⃣ APPLY LIFECYCLE EVENT (FO / MO / BO)
    // ======================================
    @PostMapping("/{tradeId}/events")
    public TradeResponseDto applyEvent(
            @PathVariable String tradeId,
            @RequestBody TradeEventRequest req) {

        Trade trade = tradeService.applyEvent(
                tradeId,
                req.getEventType()
        );

        return toDto(trade);
    }

    // ===============================
    // DTO MAPPER (Controller-level)
    // ===============================
    private TradeResponseDto toDto(Trade trade) {

        TradeResponseDto dto = new TradeResponseDto();
        dto.setTradeId(trade.getTradeId());
        dto.setInstrumentSymbol(trade.getInstrument().getSymbol());
        dto.setPortfolio(trade.getPortfolio());
        dto.setCounterparty(trade.getCounterparty());
        dto.setQuantity(trade.getQuantity());
        dto.setPrice(trade.getPrice());
        dto.setBuySell(trade.getBuySell());
        dto.setStatus(trade.getStatus());
        dto.setCreatedAt(trade.getCreatedAt());

        return dto;
    }
}
