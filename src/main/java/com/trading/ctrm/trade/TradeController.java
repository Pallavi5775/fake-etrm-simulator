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
import com.trading.ctrm.trade.dto.MultiLegTradeRequest;
import com.trading.ctrm.trade.dto.ValuationResponseDto;
import com.trading.ctrm.trade.dto.TradeEventDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

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

    /**
     * Get trade by tradeId
     */
    @GetMapping("/{tradeId}")
    public TradeResponseDto getTrade(@PathVariable String tradeId) {
        Trade trade = tradeService.findByTradeId(tradeId);
        
        // If it's a multi-leg trade, fetch and attach legs
        if (Boolean.TRUE.equals(trade.getIsMultiLeg())) {
            List<TradeLeg> legs = tradeService.getTradeLegs(tradeId);
            trade.setLegs(legs);
        }
        
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
    // 4️⃣ GET VALUATIONS FOR TRADE (Single or Multi-Leg)
    // ===============================
    @GetMapping("/{tradeId}/valuations")
    public ValuationResponseDto getValuations(@PathVariable String tradeId) {
        Trade trade = tradeService.findByTradeId(tradeId);
        LocalDate valuationDate = LocalDate.now();
        
        ValuationResponseDto response = new ValuationResponseDto();
        response.setTradeId(tradeId);
        response.setValuationDate(valuationDate);
        response.setIsMultiLeg(trade.getIsMultiLeg());
        response.setStrategyType(trade.getStrategyType() != null ? trade.getStrategyType().name() : null);
        
        if (Boolean.TRUE.equals(trade.getIsMultiLeg())) {
            // Multi-leg valuation
            List<TradeLeg> legs = tradeService.getTradeLegs(tradeId);
            List<ValuationResponseDto.LegValuationDto> legValuations = new ArrayList<>();
            BigDecimal totalMtm = BigDecimal.ZERO;
            
            for (TradeLeg leg : legs) {
                ValuationResponseDto.LegValuationDto legVal = new ValuationResponseDto.LegValuationDto();
                legVal.setLegNumber(leg.getLegNumber());
                legVal.setInstrumentCode(leg.getInstrument().getInstrumentCode());
                legVal.setBuySell(leg.getBuySell().name());
                legVal.setQuantity(leg.getQuantity());
                legVal.setTradePrice(leg.getPrice());
                legVal.setDeliveryDate(leg.getDeliveryDate());
                
                try {
                    // Get market price from forward curve
                    LocalDate deliveryDate = leg.getDeliveryDate() != null ? 
                        leg.getDeliveryDate() : trade.getTradeDate();
                    
                    com.trading.ctrm.trade.ForwardCurve curve = tradeService.findLatestForwardCurve(
                        leg.getInstrument(), deliveryDate);
                    
                    BigDecimal marketPrice = BigDecimal.valueOf(curve.getPrice());
                    legVal.setMarketPrice(marketPrice);
                    
                    // Calculate MTM for this leg
                    BigDecimal priceDiff = marketPrice.subtract(leg.getPrice());
                    BigDecimal legMtm = priceDiff.multiply(leg.getQuantity());
                    
                    // Apply buy/sell direction
                    if (leg.getBuySell() == com.trading.ctrm.trade.EnumType.BuySell.SELL) {
                        legMtm = legMtm.negate();
                    }
                    
                    // Apply ratio
                    legMtm = legMtm.multiply(leg.getRatio());
                    
                    legVal.setMtm(legMtm);
                    legVal.setPnl(legMtm);
                    
                    totalMtm = totalMtm.add(legMtm);
                } catch (Exception e) {
                    legVal.setMarketPrice(leg.getPrice());
                    legVal.setMtm(BigDecimal.ZERO);
                    legVal.setPnl(BigDecimal.ZERO);
                }
                
                legValuations.add(legVal);
            }
            
            response.setLegValuations(legValuations);
            response.setTotalMtm(totalMtm);
            response.setTotalPnl(totalMtm);
            response.setPricingModel("ForwardCurve");
            
        } else {
            // Single leg valuation
            ValuationResponseDto.LegValuationDto singleVal = new ValuationResponseDto.LegValuationDto();
            singleVal.setLegNumber(1);
            singleVal.setInstrumentCode(trade.getInstrument().getInstrumentCode());
            singleVal.setBuySell(trade.getBuySell().name());
            singleVal.setQuantity(trade.getQuantity());
            singleVal.setTradePrice(trade.getPrice());
            singleVal.setDeliveryDate(trade.getTradeDate());
            
            try {
                LocalDate deliveryDate = trade.getTradeDate() != null ? 
                    trade.getTradeDate() : LocalDate.now();
                
                BigDecimal mtm = pricingService.calculateMTM(trade, deliveryDate);
                
                // Get market price
                com.trading.ctrm.trade.ForwardCurve curve = tradeService.findLatestForwardCurve(
                    trade.getInstrument(), deliveryDate);
                
                singleVal.setMarketPrice(BigDecimal.valueOf(curve.getPrice()));
                singleVal.setMtm(mtm);
                singleVal.setPnl(mtm);
                
                response.setSingleLegValuation(singleVal);
                response.setTotalMtm(mtm);
                response.setTotalPnl(mtm);
                response.setPricingModel("ForwardCurve");
            } catch (Exception e) {
                singleVal.setMarketPrice(trade.getPrice());
                singleVal.setMtm(trade.getMtm() != null ? trade.getMtm() : BigDecimal.ZERO);
                singleVal.setPnl(trade.getMtm() != null ? trade.getMtm() : BigDecimal.ZERO);
                
                response.setSingleLegValuation(singleVal);
                response.setTotalMtm(trade.getMtm() != null ? trade.getMtm() : BigDecimal.ZERO);
                response.setTotalPnl(trade.getMtm() != null ? trade.getMtm() : BigDecimal.ZERO);
                response.setPricingModel("Stored");
            }
        }
        
        return response;
    }

    // ===============================
    // 5️⃣ APPROVE / REJECT TRADE
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
        
        // Multi-leg trade fields
        dto.setIsMultiLeg(trade.getIsMultiLeg());
        dto.setStrategyType(trade.getStrategyType());
        if (trade.getLegs() != null && !trade.getLegs().isEmpty()) {
            dto.setLegs(trade.getLegs().stream()
                .map(this::toLegDto)
                .collect(java.util.stream.Collectors.toList()));
        }

        return dto;
    }
    
    /**
     * Convert TradeLeg entity to DTO
     */
    private com.trading.ctrm.trade.dto.TradeLegDto toLegDto(TradeLeg leg) {
        com.trading.ctrm.trade.dto.TradeLegDto dto = new com.trading.ctrm.trade.dto.TradeLegDto();
        dto.setLegNumber(leg.getLegNumber());
        dto.setInstrumentCode(leg.getInstrument().getInstrumentCode());
        dto.setBuySell(leg.getBuySell());
        dto.setQuantity(leg.getQuantity());
        dto.setPrice(leg.getPrice());
        dto.setRatio(leg.getRatio());
        dto.setDeliveryDate(leg.getDeliveryDate());
        dto.setMtm(leg.getMtm());
        return dto;
    }

    // ===============================
    // MULTI-LEG TRADING ENDPOINTS
    // ===============================
    
    /**
     * Book a multi-leg trade (spread, butterfly, straddle, etc.)
     */
    @PostMapping("/multi-leg")
    public TradeResponseDto bookMultiLegTrade(@RequestBody MultiLegTradeRequest request) {
        Trade trade = tradeService.bookMultiLegTrade(request);
        return toDto(trade);
    }
    
    /**
     * Get legs for a multi-leg trade
     */
    @GetMapping("/{tradeId}/legs")
    public List<TradeLeg> getTradeLegs(@PathVariable String tradeId) {
        return tradeService.getTradeLegs(tradeId);
    }

    /**
     * Get audit trail (event history) for a trade
     */
    @GetMapping("/{tradeId}/events")
    public List<TradeEventDto> getTradeEvents(@PathVariable String tradeId) {
        return tradeService.getTradeEvents(tradeId);
    }

    /**
     * Get all available trade event types
     */
    @GetMapping("/event-types")
    public TradeEventType[] getEventTypes() {
        return TradeEventType.values();
    }

}
