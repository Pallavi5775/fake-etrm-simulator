package com.trading.ctrm.risk;

import org.springframework.stereotype.Service;

import com.trading.ctrm.pricing.ValuationHistory;
import com.trading.ctrm.pricing.ValuationHistoryRepository;
import com.trading.ctrm.trade.Trade;
import com.trading.ctrm.trade.TradeLeg;
import com.trading.ctrm.trade.TradeLegRepository;
import com.trading.ctrm.trade.ForwardCurve;
import com.trading.ctrm.trade.ForwardCurveRepository;
import com.trading.ctrm.trade.EnumType.BuySell;

import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDate;

@Service
public class PnLService {

    private final ValuationHistoryRepository valuationHistoryRepository;
    private final TradeLegRepository tradeLegRepository;
    private final ForwardCurveRepository forwardCurveRepository;

    public PnLService(
            ValuationHistoryRepository valuationHistoryRepository,
            TradeLegRepository tradeLegRepository,
            ForwardCurveRepository forwardCurveRepository) {
        this.valuationHistoryRepository = valuationHistoryRepository;
        this.tradeLegRepository = tradeLegRepository;
        this.forwardCurveRepository = forwardCurveRepository;
    }

    public BigDecimal calculatePnL(Long tradeId) {

        List<ValuationHistory> history =
                valuationHistoryRepository.findLatestTwo(tradeId);

        if (history.size() < 2) {
            return BigDecimal.ZERO;
        }

        BigDecimal latest = history.get(0).getMtm();
        BigDecimal previous = history.get(1).getMtm();

        return latest.subtract(previous);
    }

    /**
     * Calculate P&L for multi-leg trade
     * Aggregates MTM from all legs
     */
    public BigDecimal calculateMultiLegPnL(Trade trade, LocalDate valuationDate) {
        if (!Boolean.TRUE.equals(trade.getIsMultiLeg())) {
            throw new IllegalArgumentException("Trade is not a multi-leg trade");
        }

        List<TradeLeg> legs = tradeLegRepository.findByTradeIdOrderByLegNumber(trade.getTradeId());
        BigDecimal totalMtm = BigDecimal.ZERO;

        for (TradeLeg leg : legs) {
            // Get forward curve for leg's instrument and delivery date
            LocalDate deliveryDate = leg.getDeliveryDate() != null ? 
                leg.getDeliveryDate() : trade.getTradeDate();
            
            ForwardCurve curve = forwardCurveRepository
                .findLatestByInstrumentAndDeliveryDate(leg.getInstrument(), deliveryDate)
                .orElseThrow(() -> new RuntimeException(
                    "Forward curve not found for " + leg.getInstrument().getInstrumentCode() + 
                    " on " + deliveryDate));

            BigDecimal marketPrice = BigDecimal.valueOf(curve.getPrice());
            BigDecimal legMtm = marketPrice.subtract(leg.getPrice()).multiply(leg.getQuantity());
            
            // Apply BUY/SELL direction
            if (leg.getBuySell() == BuySell.SELL) {
                legMtm = legMtm.negate();
            }
            
            // Apply ratio for complex spreads
            legMtm = legMtm.multiply(leg.getRatio());
            
            totalMtm = totalMtm.add(legMtm);
            
            // Update leg MTM
            leg.setMtm(legMtm);
        }

        return totalMtm;
    }
}