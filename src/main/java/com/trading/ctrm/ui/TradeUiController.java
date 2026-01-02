package com.trading.ctrm.ui;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.trading.ctrm.lifestyle.TradeLifecycleEngine;
import com.trading.ctrm.risk.PnLService;
import com.trading.ctrm.trade.Trade;
import com.trading.ctrm.trade.TradeRepository;
import com.trading.ctrm.trade.TradeStatus;

/**
 * Trade UI Controller
 * - Approval Dashboard
 * - Approve / Reject actions
 */
@RestController
@RequestMapping("/api/ui/trades")
public class TradeUiController {

    private final TradeRepository tradeRepository;
    private final TradeLifecycleEngine lifecycleEngine;

    // ✅ Constructor injection (no field injection)
   private final PnLService pnlService;

public TradeUiController(
        TradeRepository tradeRepository,
        TradeLifecycleEngine lifecycleEngine,
        PnLService pnlService
) {
    this.tradeRepository = tradeRepository;
    this.lifecycleEngine = lifecycleEngine;
    this.pnlService = pnlService;
}

    /**
     * 🔍 Fetch all trades pending approval (UI dashboard)
     */
    @GetMapping("/pending-approvals")
    public List<TradeApprovalView> getPendingApprovals() {

        return tradeRepository.findByStatus(TradeStatus.PENDING_APPROVAL)
                .stream()
                .map(TradeApprovalView::from)
                .toList();
    }

    /**
     * ✅ Approve a trade
     */
    @PostMapping("/{tradeId}/approve")
    public Trade approveTrade(
            @PathVariable String tradeId,
            @RequestParam String role,
            @RequestParam String approvedBy
    ) {
        return lifecycleEngine.approveTrade(tradeId, role, approvedBy);
    }

    /**
     * ❌ Reject a trade
     */
    @PostMapping("/{tradeId}/reject")
    public Trade rejectTrade(
            @PathVariable String tradeId,
            @RequestParam String reason,
            @RequestParam String rejectedBy
    ) {
        return lifecycleEngine.rejectTrade(tradeId, reason, rejectedBy);
    }


    @GetMapping("/{tradeId}/pnl")
public BigDecimal getPnL(@PathVariable String tradeId) {
    Trade trade = findTrade(tradeId);
    return pnlService.calculatePnL(trade.getId());
}

private Trade findTrade(String tradeId) {
    // Try parsing as Long first (numeric database ID)
    try {
        Long id = Long.parseLong(tradeId);
        return tradeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Trade not found with id: " + tradeId));
    } catch (NumberFormatException e) {
        // Not a number, lookup by business trade ID
        return tradeRepository.findByTradeId(tradeId)
                .orElseThrow(() -> new IllegalArgumentException("Trade not found: " + tradeId));
    }
}
}
