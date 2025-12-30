package com.trading.ctrm.trade;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trading.ctrm.trade.dto.TradeResponseDto;


import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/trades")
@CrossOrigin
public class TradeQueryController {

    private final TradeRepository tradeRepository;
    private final TradeEventRepository tradeEventRepository;

    public TradeQueryController(TradeRepository tradeRepository, TradeEventRepository tradeEventRepository) {
        this.tradeRepository = tradeRepository;
        this.tradeEventRepository = tradeEventRepository;
    }

    // 1️⃣ All trades (Trade Blotter)
    @GetMapping
    public List<TradeResponseDto> getAllTrades() {
        return tradeRepository.findAll()
            .stream()
            .map(this::toDto)
            .toList();
    }

    private TradeResponseDto toDto(Trade trade) {
    TradeResponseDto dto = new TradeResponseDto();

    dto.setTradeId(trade.getTradeId());
    dto.setInstrumentSymbol(trade.getInstrument().getInstrumentCode());
    dto.setPortfolio(trade.getPortfolio());
    dto.setCounterparty(trade.getCounterparty());
    dto.setQuantity(trade.getQuantity());
    dto.setPrice(trade.getPrice());
    dto.setBuySell(trade.getBuySell());
    dto.setStatus(trade.getStatus());
    dto.setCreatedAt(trade.getCreatedAt());

    // ✅ FIXED: entity-based counting
    dto.setAmendCount(
        tradeEventRepository.countByTradeAndEventType(
            trade,
            TradeEventType.AMENDED
        )
    );

    return dto;
}


 

    // 2️⃣ Trades by portfolio
    @GetMapping("/portfolio/{portfolio}")
    public List<Trade> getTradesByPortfolio(@PathVariable String portfolio) {
        return this.tradeRepository.findByPortfolio(portfolio);
    }

    // 3️⃣ Trades by counterparty
    @GetMapping("/counterparty/{counterparty}")
    public List<Trade> getTradesByCounterparty(@PathVariable String counterparty) {
        return this.tradeRepository.findByCounterparty(counterparty);
    }

    // 4️⃣ Trades by status
    @GetMapping("/status/{status}")
    public List<Trade> getTradesByStatus(@PathVariable TradeStatus status) {
        return this.tradeRepository.findByStatus(status);
    }
}


// GET /api/trades
// GET /api/trades/portfolio/POWERDESK
// GET /api/trades/counterparty/ABC_UTILITIES
// GET /api/trades/status/CREATED