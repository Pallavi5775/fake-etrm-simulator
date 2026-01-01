package com.trading.ctrm.trade;

import org.springframework.stereotype.Service;

import com.trading.ctrm.deals.DealTemplate;
import com.trading.ctrm.deals.DealTemplateRepository;
import com.trading.ctrm.instrument.Instrument;
import com.trading.ctrm.lifestyle.TradeLifecycleEngine;
import com.trading.ctrm.rules.ApprovalExecutionService;
import com.trading.ctrm.rules.ApprovalRouting;
import com.trading.ctrm.rules.ApprovalRule;
import com.trading.ctrm.rules.ApprovalRuleEngine;
import com.trading.ctrm.rules.TradeContext;
import com.trading.ctrm.trade.EnumType.BuySell;
import com.trading.ctrm.trade.dto.TradeEventRequest;

import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

import org.springframework.lang.NonNull;

@Service
public class TradeService {

    private final TradeRepository tradeRepository;
    private final InstrumentRepository instrumentRepository;
    private final PositionService positionService;
    private final TradeLifecycleEngine tradeLifecycleEngine;
    private final DealTemplateRepository templateRepo;
    private final ApprovalRuleEngine approvalRuleEngine;
    private final ApprovalExecutionService approvalExecutionService;

    public TradeService(
            TradeRepository tradeRepository,
            InstrumentRepository instrumentRepository,
            PositionService positionService,
            TradeLifecycleEngine tradeLifecycleEngine,
            DealTemplateRepository templateRepo,
            ApprovalRuleEngine approvalRuleEngine,
            ApprovalExecutionService approvalExecutionService
    ) {
        this.tradeRepository = tradeRepository;
        this.instrumentRepository = instrumentRepository;
        this.positionService = positionService;
        this.tradeLifecycleEngine = tradeLifecycleEngine;
        this.templateRepo = templateRepo;
        this.approvalRuleEngine = approvalRuleEngine;
        this.approvalExecutionService = approvalExecutionService;
    }

    public Trade bookTrade(TradeEventRequest req) {
        if (req.getInstrumentSymbol() == null || req.getInstrumentSymbol().isBlank()) {
            throw new IllegalArgumentException("Instrument symbol is required");
        }

        Instrument instrument = instrumentRepository
                .findByInstrumentCode(req.getInstrumentSymbol());
                

        Trade trade = new Trade();
        trade.setTradeId(req.getTradeId());
        trade.setInstrument(instrument);
        trade.setPortfolio(req.getPortfolio());
        trade.setCounterparty(req.getCounterparty());
        trade.setQuantity(req.getQuantity());
        trade.setPrice(req.getPrice());
        trade.setBuySell(req.getBuySell());

        // ✅ SET INITIAL STATUS
        trade.setStatus(TradeStatus.CREATED);

        Trade saved = tradeRepository.save(trade);

        // Now apply lifecycle event
        tradeLifecycleEngine.applyEvent(
                saved,
                TradeEventType.CREATED,
                getCurrentUser(),
                "UI"
        );

        positionService.updatePosition(saved);
        return saved;
    }
    


@Transactional
public Trade bookFromTemplate(
        Long templateId,
        BigDecimal quantityOverride,
        BuySell buySell,
        String counterparty,
        String portfolio
) {

    DealTemplate template = templateRepo.findById(templateId)
        .orElseThrow(() -> new IllegalArgumentException("DealTemplate not found"));

    Trade trade = new Trade();

    // ===== TEMPLATE FIELDS =====
    trade.setInstrument(template.getInstrument());
    trade.setPrice(template.getDefaultPrice());
    trade.setTemplateId(template.getId());

    // ===== FO INPUT =====
    trade.setBuySell(buySell);
    trade.setCounterparty(counterparty);
    trade.setPortfolio(portfolio);
    trade.setQuantity(
        quantityOverride != null
            ? quantityOverride
            : template.getDefaultQuantity()
    );

    // ===== SYSTEM FIELDS =====
    trade.setTradeId(UUID.randomUUID().toString());
    trade.setCreatedAt(LocalDateTime.now());
    trade.setStatus(TradeStatus.CREATED);

    // ===== AUTO-APPROVAL CHECK =====
    if (template.isAutoApprovalAllowed()) {
        trade.setStatus(TradeStatus.APPROVED);
        trade.setPendingApprovalRole(null);
        return tradeRepository.save(trade);
    }

    // ===== APPROVAL EVALUATION =====
    TradeContext ctx = new TradeContext();
    ctx.setTradeId(trade.getTradeId());
    ctx.setQuantity(trade.getQuantity().doubleValue());
    ctx.setPrice(trade.getPrice().doubleValue());
    ctx.setCounterparty(trade.getCounterparty());
    ctx.setPortfolio(trade.getPortfolio());
    ctx.setInstrumentType(
        trade.getInstrument().getInstrumentType().name()
    );

    System.out.println("🔍 Evaluating approval rules for trade: " + trade.getTradeId());
    System.out.println("   Quantity: " + ctx.getQuantity());
    System.out.println("   Price: " + ctx.getPrice());
    System.out.println("   Counterparty: " + ctx.getCounterparty());
    System.out.println("   Portfolio: " + ctx.getPortfolio());
    System.out.println("   InstrumentType: " + ctx.getInstrumentType());

    Optional<ApprovalRule> matchedRule =
        approvalRuleEngine.evaluate(ctx, "TRADE_BOOK");

    System.out.println("   Rule matched: " + matchedRule.isPresent());

    if (matchedRule.isPresent()) {

        ApprovalRule rule = matchedRule.get();

        approvalExecutionService.createPendingApproval(
            ctx,
            rule,
            "TRADE_BOOK"
        );

        trade.setStatus(TradeStatus.PENDING_APPROVAL);
        trade.setMatchedRuleId(rule.getRuleId());
        trade.setCurrentApprovalLevel(1);
        trade.setPendingApprovalRole(
            rule.getRouting().stream()
                .filter(r -> r.getApprovalLevel() == 1)
                .findFirst()
                .map(ApprovalRouting::getApprovalRole)
                .orElse(null)
        );

    } else {
        // No matching rule found - trade requires manual approval
        trade.setStatus(TradeStatus.PENDING_APPROVAL);
        trade.setPendingApprovalRole("SENIOR_TRADER"); // Default approver when no rule matches
    }

    return tradeRepository.save(trade);
}



    public Trade applyEvent(String tradeId, TradeEventType eventType) {

        Trade trade = tradeRepository
                .findByTradeId(tradeId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Trade not found: " + tradeId)
                );

        Trade updated = tradeLifecycleEngine.applyEvent(
                trade,
                eventType,
                getCurrentUser(),
                "UI"
        );

        if (eventType == TradeEventType.AMENDED ||
            eventType == TradeEventType.PRICED) {

            positionService.updatePosition(updated);
        }

        return updated;
    }

    private String getCurrentUser() {
        return "FO_USER";
    }

    public Trade findByTradeId(String tradeId) {
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
