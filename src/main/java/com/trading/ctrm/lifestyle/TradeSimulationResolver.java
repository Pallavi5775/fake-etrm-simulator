package com.trading.ctrm.lifestyle;
import com.trading.ctrm.trade.Trade;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.trading.ctrm.trade.TradeRepository;

@Service
public class TradeSimulationResolver {

    private final TradeRepository tradeRepo;

    public TradeSimulationResolver(TradeRepository tradeRepo) {
        this.tradeRepo = tradeRepo;
    }

    public List<Trade> resolveTrades(SimulationRequest req) {

        return switch (req.getScope()) {

            case SINGLE_TRADE -> List.of(
                tradeRepo.findByTradeId(req.getTradeId())
                    .orElseThrow(() ->
                        new IllegalArgumentException("Trade not found: " + req.getTradeId()))
            );

            case FILTERED_TRADES -> tradeRepo.findByFilters(
                req.getDesk(),
                req.getStatus(),
                req.getInstrument(),
                req.getCounterparty()
            );

            

           case SAMPLE_TRADES -> tradeRepo.findSampleTrades(
        req.getDesk(),
        PageRequest.of(
                0,
                req.getSampleSize() != null ? req.getSampleSize() : 10
        )
);
        };
    }
}

