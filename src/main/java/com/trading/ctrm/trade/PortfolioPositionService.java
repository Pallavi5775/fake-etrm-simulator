package com.trading.ctrm.trade;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.trading.ctrm.common.PortfolioPosition;
import com.trading.ctrm.instrument.Instrument;
import com.trading.ctrm.trade.EnumType.BuySell;

@Service
public class PortfolioPositionService {

    private final PortfolioPositionRepository repo;

    public PortfolioPositionService(PortfolioPositionRepository repo) {
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
                BigDecimal.ZERO
            )
        );

    BigDecimal signedQty =
            trade.getBuySell() == BuySell.BUY
                    ? trade.getQuantity()
                    : trade.getQuantity().negate();

    position.setNetQuantity(
        position.getNetQuantity().add(signedQty)
);
    repo.save(position);
}

    /**
     * Update position for a specific leg of a multi-leg trade
     */
    public void updatePositionForLeg(Trade trade, TradeLeg leg) {
        String portfolio = trade.getPortfolio();
        Instrument instrument = leg.getInstrument();

        PortfolioPosition position = repo
            .findByPortfolioAndInstrument(portfolio, instrument)
            .orElseGet(() ->
                new PortfolioPosition(
                    portfolio,
                    instrument,
                    BigDecimal.ZERO
                )
            );

        BigDecimal signedQty = leg.getBuySell() == BuySell.BUY
                ? leg.getQuantity()
                : leg.getQuantity().negate();

        position.setNetQuantity(
            position.getNetQuantity().add(signedQty)
        );
        
        repo.save(position);
    }

}
