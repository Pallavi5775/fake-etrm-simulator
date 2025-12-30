package com.trading.ctrm.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.trading.ctrm.trade.EnumType.BuySell;
import com.trading.ctrm.trade.ForwardCurve;
import com.trading.ctrm.trade.ForwardCurveRepository;
import com.trading.ctrm.trade.InstrumentRepository;
import com.trading.ctrm.trade.Trade;
import com.trading.ctrm.instrument.Instrument;


@Service
public class PricingService {

    private final ForwardCurveRepository curveRepo;
    private final InstrumentRepository instrumentRepo;

    public PricingService(
            ForwardCurveRepository curveRepo,
            InstrumentRepository instrumentRepo
    ) {
        this.curveRepo = curveRepo;
        this.instrumentRepo = instrumentRepo;
    }

    public double getForwardPrice(String symbol, LocalDate date) {

        Instrument instrument = instrumentRepo
            .findByInstrumentCode(symbol);
            

        return curveRepo.findByInstrumentAndDeliveryDate(instrument, date)
                   .map(ForwardCurve::getPrice)
                   .orElseThrow(() -> new IllegalStateException("No curve found"));
    }

    public BigDecimal calculateMTM(Trade trade, LocalDate deliveryDate) {

        // 1️⃣ Fetch forward curve for the trade’s instrument
        ForwardCurve curve = curveRepo
            .findByInstrumentAndDeliveryDate(
                    trade.getInstrument(),
                    deliveryDate
            )
            .orElseThrow(() ->
                new RuntimeException(
                    "Forward curve not found for "
                    + trade.getInstrument().getInstrumentCode()
                    + " on " + deliveryDate
                ));

        // 2️⃣ Signed quantity
        BigDecimal signedQty =
            trade.getBuySell() == BuySell.BUY
                    ? trade.getQuantity()
                    : trade.getQuantity().negate();

            // 3️⃣ MTM calculation
            BigDecimal pnl =
            BigDecimal.valueOf(curve.getPrice())
                    .subtract(trade.getPrice())
                    .multiply(signedQty)
                    .setScale(2, RoundingMode.HALF_UP);


            return pnl;        

        }

        
}
