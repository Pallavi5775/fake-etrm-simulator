package com.trading.ctrm.lifestyle;



import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.trading.ctrm.trade.Trade;
import com.trading.ctrm.trade.dto.ApprovalRequest;
import com.trading.ctrm.trade.dto.TradeResponseDto;
import com.trading.ctrm.lifestyle.dto.PendingApprovalDto;
import com.trading.ctrm.lifestyle.dto.RejectRequest;

@RestController
@RequestMapping("/api/approvals")
@CrossOrigin(origins = "*")
public class ApprovalController {

    private final TradeLifecycleEngine lifecycleEngine;
    private final ApprovalQueryService approvalQueryService;

    public ApprovalController(
            TradeLifecycleEngine lifecycleEngine,
            ApprovalQueryService approvalQueryService) {

        this.lifecycleEngine = lifecycleEngine;
        this.approvalQueryService = approvalQueryService;
    }

    /**
     * 🔍 Get trades pending approval for a role
     * Example:
     * GET /api/approvals/pending?role=RISK
     * Or use header: X-User-Role
     */
    @GetMapping("/pending")
    public List<PendingApprovalDto> getPendingApprovals(
            @RequestParam(required = false) String role,
            @RequestHeader(value = "X-User-Role", required = false) String userRole) {

        // Use role from query param or header
        String approvalRole = role != null ? role : userRole;
        
        if (approvalRole == null) {
            throw new IllegalArgumentException("Role must be provided either as query param or X-User-Role header");
        }

        return approvalQueryService.findPendingApprovals(approvalRole);
    }

    /**
     * ✅ Approve a trade
     * POST /api/approvals/{tradeId}/approve
     */
    @PostMapping("/{tradeId}/approve")
    public TradeResponseDto approve(
            @PathVariable String tradeId,
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @RequestHeader(value = "X-User-Name", required = false) String userName,
            Principal principal) {

        // Get user info from headers (set by frontend from localStorage)
        String approverRole = userRole != null ? userRole : "RISK";
        String approvedBy = userName != null ? userName : 
                          (principal != null ? principal.getName() : "SYSTEM");

        Trade trade = lifecycleEngine.approveTrade(tradeId, approverRole, approvedBy);
        return toDto(trade);
    }

    /**
     * ❌ Reject a trade
     * POST /api/approvals/{tradeId}/reject
     */
    @PostMapping("/{tradeId}/reject")
    public TradeResponseDto reject(
            @PathVariable String tradeId,
            @RequestBody RejectRequest request,
            Principal principal) {

        String rejectedBy = principal != null ? principal.getName() : "SYSTEM";

        Trade trade = lifecycleEngine.rejectTrade(
                tradeId,
                request.getReason(),
                rejectedBy
        );
        return toDto(trade);
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
        dto.setPendingApprovalRole(trade.getPendingApprovalRole());
        dto.setCurrentApprovalLevel(trade.getCurrentApprovalLevel());
        dto.setMatchedRuleId(trade.getMatchedRuleId());
        return dto;
    }
}

