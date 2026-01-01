package com.trading.ctrm.lifestyle;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.trading.ctrm.lifestyle.dto.PendingApprovalDto;
import com.trading.ctrm.trade.Trade;
import com.trading.ctrm.trade.TradeRepository;
import com.trading.ctrm.trade.TradeStatus;

@Service
public class ApprovalQueryService {

    private final TradeRepository tradeRepository;

    public ApprovalQueryService(TradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    public List<PendingApprovalDto> findPendingApprovals(String role) {

        return tradeRepository
                .findByStatusAndPendingApprovalRole(
                        TradeStatus.PENDING_APPROVAL,
                        role
                )
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private PendingApprovalDto toDto(Trade trade) {
        PendingApprovalDto dto = new PendingApprovalDto();
        dto.setTradeId(trade.getId());
        dto.setBusinessTradeId(trade.getTradeId());
        dto.setDesk(trade.getPortfolio());
        dto.setStatus(trade.getStatus().name());
        dto.setRequiredRole(trade.getPendingApprovalRole());
        dto.setCurrentApprovalLevel(trade.getCurrentApprovalLevel());
        dto.setMatchedRuleId(trade.getMatchedRuleId());
        dto.setRequestedAt(trade.getUpdatedAt());
        dto.setEvent("APPROVAL_REQUIRED");
        return dto;
    }
}
