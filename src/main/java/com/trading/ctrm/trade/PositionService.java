package com.trading.ctrm.trade;

import org.springframework.stereotype.Service;

import com.trading.ctrm.common.PortfolioPosition;
import com.trading.ctrm.instrument.Instrument;
import com.trading.ctrm.trade.EnumType.BuySell;

@Service
public class PositionService {

    private final PortfolioPositionRepository repo;

    public PositionService(PortfolioPositionRepository repo) {
        this.repo = repo;
    }

    public void updatePosition(Trade trade) {

    Instrument instrument = trade.getInstrument();
    String portfolio = trade.getPortfolio();

    PortfolioPosition position = repo
        .findByPortfolioAndInstrument(portfolio, instrument)
        .orElseGet(() ->
            new PortfolioPosition(
                portfolio,
                instrument,
                0.0
            )
        );

    double signedQty =
            trade.getBuySell() == BuySell.BUY
                    ? trade.getQuantity()
                    : -trade.getQuantity();

    position.setNetQuantity(
            position.getNetQuantity() + signedQty
    );

    repo.save(position);
}

}
