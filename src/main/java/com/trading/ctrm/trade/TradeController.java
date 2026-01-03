package com.trading.ctrm.trade;

import com.trading.ctrm.lifestyle.TradeLifecycleEngine;
import com.trading.ctrm.pricing.PricingService;
import com.trading.ctrm.risk.PnLService;
import com.trading.ctrm.rules.ApprovalRuleRepository;
import com.trading.ctrm.trade.EnumType.BuySell;
import com.trading.ctrm.trade.dto.ApprovalRequest;
import com.trading.ctrm.trade.dto.RejectRequest;
import com.trading.ctrm.trade.dto.TradeEventRequest;
import com.trading.ctrm.trade.dto.TradeResponseDto;
import com.trading.ctrm.trade.dto.BookFromTemplateRequest;
import com.trading.ctrm.trade.dto.AmendTradeRequest;
import com.trading.ctrm.trade.dto.SettleTradeRequest;
import com.trading.ctrm.trade.dto.CancelTradeRequest;

import java.math.BigDecimal;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trades")
public class TradeController {

    private final TradeService tradeService;
    private final PnLService pnlService;
    private final TradeLifecycleEngine lifecycleEngine;
    private final ApprovalRuleRepository approvalRuleRepository;
    private final PricingService pricingService;

    public TradeController(
            TradeService tradeService,
            PnLService pnlService,
            TradeLifecycleEngine lifecycleEngine,
            ApprovalRuleRepository approvalRuleRepository,
            PricingService pricingService) {
        this.tradeService = tradeService;
        this.pnlService = pnlService;
        this.lifecycleEngine = lifecycleEngine;
        this.approvalRuleRepository = approvalRuleRepository;
        this.pricingService = pricingService;
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

    // @PreAuthorize("hasAnyRole('SENIOR_TRADER', 'HEAD_TRADER', 'ADMIN')")
    @PostMapping("/book-from-template")
    public TradeResponseDto bookFromTemplate(@RequestBody BookFromTemplateRequest request) {
        Trade trade = tradeService.bookFromTemplate(
            request.getTemplateId(),
            request.getQuantity(),
            request.getBuySell(),
            request.getCounterparty(),
            request.getPortfolio(),
            request.getCreatedByUser(),
            request.getTradeDate(),
            request.getValuationConfig()
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
        
        // Calculate MTM using forward curve pricing
        try {
            // Use trade date as delivery date (or you can use a specific delivery date)
            java.time.LocalDate deliveryDate = trade.getTradeDate() != null 
                ? trade.getTradeDate() 
                : java.time.LocalDate.now();
            
            BigDecimal mtm = pricingService.calculateMTM(trade, deliveryDate);
            
            // Update trade MTM if not already set
            if (trade.getMtm() == null || !trade.getMtm().equals(mtm)) {
                trade.setMtm(mtm);
                tradeService.saveTrade(trade);
            }
            
            return mtm;
        } catch (Exception e) {
            // If forward curve not found, return stored MTM or zero
            return trade.getMtm() != null ? trade.getMtm() : BigDecimal.ZERO;
        }
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
    // 5️⃣ AMEND TRADE
    // ===============================
    @PostMapping("/{tradeId}/amend")
    public TradeResponseDto amendTrade(
            @PathVariable String tradeId,
            @RequestBody AmendTradeRequest request) {
        
        Trade trade = tradeService.findByTradeId(tradeId);
        
        // Store original values to check materiality
        BigDecimal originalPrice = trade.getPrice();
        BigDecimal originalQuantity = trade.getQuantity();
        boolean isMaterialChange = false;
        
        // Update trade details and check for material changes
        if (request.getPrice() != null && !request.getPrice().equals(originalPrice)) {
            BigDecimal priceChangePct = request.getPrice()
                .subtract(originalPrice)
                .divide(originalPrice, 4, BigDecimal.ROUND_HALF_UP)
                .abs()
                .multiply(BigDecimal.valueOf(100));
            
            trade.setPrice(request.getPrice());
            
            // Material if price change > 5%
            if (priceChangePct.compareTo(BigDecimal.valueOf(5)) > 0) {
                isMaterialChange = true;
            }
        }
        
        if (request.getQuantity() != null && !request.getQuantity().equals(originalQuantity)) {
            BigDecimal qtyChangePct = request.getQuantity()
                .subtract(originalQuantity)
                .divide(originalQuantity, 4, BigDecimal.ROUND_HALF_UP)
                .abs()
                .multiply(BigDecimal.valueOf(100));
            
            trade.setQuantity(request.getQuantity());
            
            // Material if quantity change > 10%
            if (qtyChangePct.compareTo(BigDecimal.valueOf(10)) > 0) {
                isMaterialChange = true;
            }
        }
        
        // If material change, require re-approval
        if (isMaterialChange) {
            trade.setStatus(TradeStatus.PENDING_APPROVAL);
            trade.setPendingApprovalRole("RISK"); // Reset approval workflow
            trade.setCurrentApprovalLevel(1);
            Trade savedTrade = tradeService.saveTrade(trade);
            return toDto(savedTrade);
        }
        
        // Non-material change: auto-approve
        tradeService.saveTrade(trade);
        
        // Apply lifecycle event (this will handle status transition to AMENDED)
        Trade amendedTrade = tradeService.applyEvent(tradeId, TradeEventType.AMENDED);
        
        return toDto(amendedTrade);
    }

    // ===============================
    // 6️⃣ SETTLE TRADE
    // ===============================
    @PostMapping("/{tradeId}/settle")
    public TradeResponseDto settleTrade(
            @PathVariable String tradeId,
            @RequestBody(required = false) SettleTradeRequest request) {
        
        // Apply lifecycle event (this will handle status transition to SETTLED)
        Trade settledTrade = tradeService.applyEvent(tradeId, TradeEventType.SETTLED);
        
        return toDto(settledTrade);
    }

    // ===============================
    // 7️⃣ CANCEL TRADE
    // ===============================
    @PostMapping("/{tradeId}/cancel")
    public TradeResponseDto cancelTrade(
            @PathVariable String tradeId,
            @RequestBody CancelTradeRequest request) {
        
        // Apply lifecycle event (this will handle status transition to CANCELLED)
        Trade cancelledTrade = tradeService.applyEvent(tradeId, TradeEventType.CANCELLED);
        
        return toDto(cancelledTrade);
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
        dto.setCreatedBy(trade.getCreatedBy());
        
        System.out.println("DEBUG: trade.getTradeDate() = " + trade.getTradeDate());
        dto.setTradeDate(trade.getTradeDate());
        System.out.println("DEBUG: dto.getTradeDate() = " + dto.getTradeDate());
        
        // Approval workflow
        dto.setPendingApprovalRole(trade.getPendingApprovalRole());
        dto.setCurrentApprovalLevel(trade.getCurrentApprovalLevel());
        dto.setMatchedRuleId(trade.getMatchedRuleId());
        
        // Fetch matched rule name if rule ID exists
        if (trade.getMatchedRuleId() != null) {
            System.out.println("DEBUG: Looking up rule with ID: " + trade.getMatchedRuleId());
            approvalRuleRepository.findById(trade.getMatchedRuleId())
                .ifPresent(rule -> {
                    System.out.println("DEBUG: Found rule: " + rule.getRuleName());
                    dto.setMatchedRuleName(rule.getRuleName());
                });
            System.out.println("DEBUG: After lookup, matchedRuleName = " + dto.getMatchedRuleName());
        }
        
        // Valuation context
        dto.setMtm(trade.getMtm());
        dto.setCommodity(trade.getInstrument().getCommodity());
        dto.setInstrumentType(trade.getInstrument().getInstrumentType().name());

        return dto;
    }
}
