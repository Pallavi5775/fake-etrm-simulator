package com.trading.ctrm.trade;

import com.trading.ctrm.lifestyle.TradeLifecycleEngine;
import com.trading.ctrm.risk.PnLService;
import com.trading.ctrm.trade.EnumType.BuySell;
import com.trading.ctrm.trade.dto.ApprovalRequest;
import com.trading.ctrm.trade.dto.RejectRequest;
import com.trading.ctrm.trade.dto.TradeEventRequest;
import com.trading.ctrm.trade.dto.TradeResponseDto;

import java.math.BigDecimal;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trades")
@CrossOrigin(origins = "*")
public class TradeController {

    private final TradeService tradeService;
    private final PnLService pnlService;
    private final TradeLifecycleEngine lifecycleEngine;

    public TradeController(TradeService tradeService, PnLService pnlService, TradeLifecycleEngine lifecycleEngine) {
        this.tradeService = tradeService;
        this.pnlService = pnlService;
        this.lifecycleEngine = lifecycleEngine;
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

    @PostMapping("/book-from-template/{templateId}")
public TradeResponseDto bookFromTemplate(
        @PathVariable Long templateId,
        @RequestParam BigDecimal quantity,
        @RequestParam BuySell buySell,
        @RequestParam String counterparty,
        @RequestParam String portfolio
) {
    Trade trade = tradeService.bookFromTemplate(
        templateId,
        quantity,
        buySell,
        counterparty,
        portfolio
    );
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
    // 3️⃣ GET PNL FOR TRADE
    // ===============================
    @GetMapping("/{tradeId}/pnl")
    public BigDecimal getPnL(@PathVariable String tradeId) {
        Trade trade = tradeService.findByTradeId(tradeId);
        return pnlService.calculatePnL(trade.getId());
    }

    // ===============================
    // 4️⃣ APPROVE / REJECT TRADE
    // ===============================
    @PostMapping("/{tradeId}/approve")
    public TradeResponseDto approveTrade(
            @PathVariable String tradeId,
            @RequestBody ApprovalRequest request) {
        
        String role = request.getRole() != null ? request.getRole() : "RISK";
        String approvedBy = request.getApprovedBy() != null ? request.getApprovedBy() : "SYSTEM";
        
        Trade trade = lifecycleEngine.approveTrade(tradeId, role, approvedBy);
        return toDto(trade);
    }

    @PostMapping("/{tradeId}/reject")
    public TradeResponseDto rejectTrade(
            @PathVariable String tradeId,
            @RequestBody RejectRequest request) {
        
        String reason = request.getReason() != null ? request.getReason() : "No reason provided";
        String rejectedBy = request.getRejectedBy() != null ? request.getRejectedBy() : "SYSTEM";
        
        Trade trade = lifecycleEngine.rejectTrade(tradeId, reason, rejectedBy);
        return toDto(trade);
    }

    // ===============================
    // DTO MAPPER (Controller-level)
    // ===============================
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

        return dto;
    }
}
