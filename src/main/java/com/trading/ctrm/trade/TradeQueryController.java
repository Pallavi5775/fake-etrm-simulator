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

public class TradeQueryController {

    private final TradeRepository tradeRepository;
    private final TradeEventRepository tradeEventRepository;

    public TradeQueryController(TradeRepository tradeRepository, TradeEventRepository tradeEventRepository) {
        this.tradeRepository = tradeRepository;
        this.tradeEventRepository = tradeEventRepository;
    }

    // 1️⃣ All trades (Trade Blotter) - supports filtering by status and role
    @GetMapping
    public List<TradeResponseDto> getAllTrades(
            @org.springframework.web.bind.annotation.RequestParam(required = false) TradeStatus status,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String pendingRole) {
        
        List<Trade> trades;
        
        if (status != null && pendingRole != null) {
            // Filter by both status and pending approval role
            trades = tradeRepository.findByStatusAndPendingApprovalRole(status, pendingRole);
        } else if (status != null) {
            // Filter by status only
            trades = tradeRepository.findByStatus(status);
        } else {
            // Return all trades
            trades = tradeRepository.findAll();
        }
            
        return trades.stream()
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
    dto.setCreatedBy(trade.getCreatedBy());
    
    // ✅ Approval workflow fields
    dto.setPendingApprovalRole(trade.getPendingApprovalRole());
    dto.setCurrentApprovalLevel(trade.getCurrentApprovalLevel());
    dto.setMatchedRuleId(trade.getMatchedRuleId());
    
    // ✅ Valuation context information
    dto.setMtm(trade.getMtm());
    dto.setCommodity(trade.getInstrument().getCommodity());
    dto.setInstrumentType(trade.getInstrument().getInstrumentType().name());

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

    // 5️⃣ Search trades by multiple criteria
    @GetMapping("/search")
    public List<TradeResponseDto> searchTrades(
            @org.springframework.web.bind.annotation.RequestParam(required = false) String portfolio,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String counterparty,
            @org.springframework.web.bind.annotation.RequestParam(required = false) TradeStatus status) {
        
        List<Trade> trades = tradeRepository.findAll();
        
        // Apply filters
        if (portfolio != null && !portfolio.isEmpty()) {
            trades = trades.stream()
                    .filter(t -> t.getPortfolio().equalsIgnoreCase(portfolio))
                    .toList();
        }
        
        if (counterparty != null && !counterparty.isEmpty()) {
            trades = trades.stream()
                    .filter(t -> t.getCounterparty().equalsIgnoreCase(counterparty))
                    .toList();
        }
        
        if (status != null) {
            trades = trades.stream()
                    .filter(t -> t.getStatus() == status)
                    .toList();
        }
        
        return trades.stream()
                .map(this::toDto)
                .toList();
    }
}


// GET /api/trades
// GET /api/trades/portfolio/POWERDESK
// GET /api/trades/counterparty/ABC_UTILITIES
// GET /api/trades/status/CREATED