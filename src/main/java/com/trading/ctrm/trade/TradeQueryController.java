package com.trading.ctrm.trade;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trading.ctrm.rules.ApprovalRuleRepository;
import com.trading.ctrm.trade.dto.TradeResponseDto;


import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/trades")

public class TradeQueryController {

    private final TradeRepository tradeRepository;
    private final TradeEventRepository tradeEventRepository;
    private final ApprovalRuleRepository approvalRuleRepository;

    public TradeQueryController(TradeRepository tradeRepository, TradeEventRepository tradeEventRepository, ApprovalRuleRepository approvalRuleRepository) {
        this.tradeRepository = tradeRepository;
        this.tradeEventRepository = tradeEventRepository;
        this.approvalRuleRepository = approvalRuleRepository;
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
    dto.setTradeDate(trade.getTradeDate());
    
    // ✅ Approval workflow fields
    dto.setPendingApprovalRole(trade.getPendingApprovalRole());
    dto.setCurrentApprovalLevel(trade.getCurrentApprovalLevel());
    dto.setMatchedRuleId(trade.getMatchedRuleId());
    
    // Fetch matched rule name if rule ID exists
    if (trade.getMatchedRuleId() != null) {
        approvalRuleRepository.findById(trade.getMatchedRuleId())
            .ifPresent(rule -> dto.setMatchedRuleName(rule.getRuleName()));
    }
    
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
    
    // 6️⃣ Get all pending approvals
    @GetMapping("/approval/pending")
    public List<TradeResponseDto> getPendingApprovals(
            @org.springframework.web.bind.annotation.RequestParam(required = false) String role) {
        
        List<Trade> trades;
        
        if (role != null && !role.isEmpty()) {
            // Filter by specific role
            trades = tradeRepository.findByStatusAndPendingApprovalRole(TradeStatus.PENDING_APPROVAL, role);
        } else {
            // All pending approvals
            trades = tradeRepository.findByStatus(TradeStatus.PENDING_APPROVAL);
        }
        
        return trades.stream()
                .map(this::toDto)
                .toList();
    }
    
    // 7️⃣ Get all available trade statuses
    @GetMapping("/statuses")
    public List<Map<String, String>> getAllStatuses() {
        return Arrays.stream(TradeStatus.values())
                .map(status -> Map.of(
                    "code", status.name(),
                    "name", formatStatusName(status.name())
                ))
                .collect(Collectors.toList());
    }
    
    private String formatStatusName(String status) {
        return Arrays.stream(status.split("_"))
                .map(word -> word.charAt(0) + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }
}


// GET /api/trades
// GET /api/trades/portfolio/POWERDESK
// GET /api/trades/counterparty/ABC_UTILITIES
// GET /api/trades/status/CREATED