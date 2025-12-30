package com.trading.ctrm.trade;

import org.springframework.stereotype.Service;

import com.trading.ctrm.deals.DealTemplate;
import com.trading.ctrm.deals.DealTemplateRepository;
import com.trading.ctrm.instrument.Instrument;
import com.trading.ctrm.lifestyle.TradeLifecycleEngine;
import com.trading.ctrm.trade.dto.TradeEventRequest;

import jakarta.transaction.Transactional;
import org.springframework.lang.NonNull;

@Service
public class TradeService {

    private final TradeRepository tradeRepository;
    private final InstrumentRepository instrumentRepository;
    private final PositionService positionService;
    private final TradeLifecycleEngine tradeLifecycleEngine;
    private final DealTemplateRepository templateRepo;

    public TradeService(
            TradeRepository tradeRepository,
            InstrumentRepository instrumentRepository,
            PositionService positionService,
            TradeLifecycleEngine tradeLifecycleEngine,
            DealTemplateRepository templateRepo
    ) {
        this.tradeRepository = tradeRepository;
        this.instrumentRepository = instrumentRepository;
        this.positionService = positionService;
        this.tradeLifecycleEngine = tradeLifecycleEngine;
        this.templateRepo = templateRepo;
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
    public Trade bookFromTemplate(@NonNull Long templateId) {

        DealTemplate template = templateRepo.findById(templateId)
                .orElseThrow(() -> new IllegalArgumentException("DealTemplate not found"));

        Trade trade = new Trade();

        // 🔑 CORE FIX: bind Instrument entity
        trade.setInstrument(template.getInstrument());

        // Defaults from template
        trade.setQuantity(template.getDefaultQuantity());
        trade.setPrice(template.getDefaultPrice());

        // Status always starts as NEW
        trade.setStatus(TradeStatus.CREATED);

        // Optional: auto-approval logic
        if (template.isAutoApprovalAllowed()) {
            trade.setStatus(TradeStatus.APPROVED);
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

    
}
