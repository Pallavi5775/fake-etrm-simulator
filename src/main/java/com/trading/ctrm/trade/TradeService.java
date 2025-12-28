package com.trading.ctrm.trade;

import org.springframework.stereotype.Service;

import com.trading.ctrm.instrument.Instrument;
import com.trading.ctrm.lifestyle.TradeLifecycleEngine;
import com.trading.ctrm.trade.dto.TradeEventRequest;

@Service
public class TradeService {

    private final TradeRepository tradeRepository;
    private final InstrumentRepository instrumentRepository;
    private final PositionService positionService;
    private final TradeLifecycleEngine tradeLifecycleEngine;

    public TradeService(
            TradeRepository tradeRepository,
            InstrumentRepository instrumentRepository,
            PositionService positionService,
            TradeLifecycleEngine tradeLifecycleEngine
    ) {
        this.tradeRepository = tradeRepository;
        this.instrumentRepository = instrumentRepository;
        this.positionService = positionService;
        this.tradeLifecycleEngine = tradeLifecycleEngine;
    }

    public Trade bookTrade(TradeEventRequest req) {
        if (req.getInstrumentSymbol() == null || req.getInstrumentSymbol().isBlank()) {
            throw new IllegalArgumentException("Instrument symbol is required");
        }

        Instrument instrument = instrumentRepository
                .findBySymbol(req.getInstrumentSymbol())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Instrument not found: " + req.getInstrumentSymbol())
                );

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
