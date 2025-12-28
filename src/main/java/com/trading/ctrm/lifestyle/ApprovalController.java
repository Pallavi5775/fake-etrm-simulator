package com.trading.ctrm.lifestyle;



import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.trading.ctrm.trade.Trade;
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
     */
    @GetMapping("/pending")
    public List<PendingApprovalDto> getPendingApprovals(
            @RequestParam String role) {

        return approvalQueryService.findPendingApprovals(role);
    }

    /**
     * ✅ Approve a trade
     * POST /api/approvals/{tradeId}/approve
     */
    @PostMapping("/{tradeId}/approve")
    public Trade approve(
            @PathVariable Long tradeId,
            @RequestParam String role,
            Principal principal) {

        return lifecycleEngine.approveTrade(
                tradeId,
                role,
                principal.getName()
        );
    }

    /**
     * ❌ Reject a trade
     * POST /api/approvals/{tradeId}/reject
     */
    @PostMapping("/{tradeId}/reject")
    public Trade reject(
            @PathVariable Long tradeId,
            @RequestBody RejectRequest request,
            Principal principal) {

        return lifecycleEngine.rejectTrade(
                tradeId,
                request.getReason(),
                principal.getName()
        );
    }
}

