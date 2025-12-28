package com.trading.ctrm.pricing;

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
            .findBySymbol(symbol)
            .orElseThrow(() -> new IllegalArgumentException("Invalid instrument"));

        return curveRepo.findByInstrumentAndDeliveryDate(instrument, date)
                   .map(ForwardCurve::getPrice)
                   .orElseThrow(() -> new IllegalStateException("No curve found"));
    }

    public double calculateMTM(Trade trade, LocalDate deliveryDate) {

        // 1️⃣ Fetch forward curve for the trade’s instrument
        ForwardCurve curve = curveRepo
            .findByInstrumentAndDeliveryDate(
                    trade.getInstrument(),
                    deliveryDate
            )
            .orElseThrow(() ->
                new RuntimeException(
                    "Forward curve not found for "
                    + trade.getInstrument().getSymbol()
                    + " on " + deliveryDate
                ));

        // 2️⃣ Signed quantity
        double signedQty =
                trade.getBuySell() == BuySell.BUY
                        ? trade.getQuantity()
                        : -trade.getQuantity();

        // 3️⃣ MTM calculation
        return (curve.getPrice() - trade.getPrice()) * signedQty;
    }
}
