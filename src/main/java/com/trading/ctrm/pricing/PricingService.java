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
import com.trading.ctrm.instrument.InstrumentType;
import com.trading.ctrm.yieldcurve.YieldCurveRepository;
import com.trading.ctrm.yieldcurve.YieldCurve;
import com.trading.ctrm.rules.ValuationContext;
import com.trading.ctrm.rules.TradeContext;
import com.trading.ctrm.rules.MarketContext;
import com.trading.ctrm.rules.PricingContext;
import com.trading.ctrm.rules.RiskContext;
import com.trading.ctrm.rules.AccountingContext;
import com.trading.ctrm.rules.CreditContext;
import com.trading.ctrm.rules.AuditContext;


@Service
public class PricingService {

    private final ForwardCurveRepository curveRepo;
    private final InstrumentRepository instrumentRepo;
    private final YieldCurveRepository yieldCurveRepository;
    private final PricingEngineFactory pricingEngineFactory;

    public PricingService(
            ForwardCurveRepository curveRepo,
            InstrumentRepository instrumentRepo,
            YieldCurveRepository yieldCurveRepository,
            PricingEngineFactory pricingEngineFactory
    ) {
        this.curveRepo = curveRepo;
        this.instrumentRepo = instrumentRepo;
        this.yieldCurveRepository = yieldCurveRepository;
        this.pricingEngineFactory = pricingEngineFactory;
    }

    public double getForwardPrice(String symbol, LocalDate date) {

        Instrument instrument = instrumentRepo
            .findByInstrumentCode(symbol);
            

        return curveRepo.findByInstrumentAndDeliveryDate(instrument, date)
                   .map(ForwardCurve::getPrice)
                   .orElseThrow(() -> new IllegalStateException("No curve found"));
    }

    public BigDecimal calculateMTM(Trade trade, LocalDate deliveryDate) {
        // Force load instrument if it's lazy loaded
        Instrument instrument = trade.getInstrument();
        String instrumentType = instrument.getInstrumentType() != null ? 
            instrument.getInstrumentType().name() : "null";
        String instrumentCode = instrument.getInstrumentCode();
        
        System.out.println("[PricingService] Calculating MTM for " + instrumentCode + " (type: " + instrumentType + ") on " + deliveryDate);

        // Get the appropriate pricing engine based on instrument type
        PricingEngine engine = pricingEngineFactory.getEngine(instrument);

        // Build valuation context with market data
        ValuationContext context = ValuationContext.builder()
            .trade(TradeContext.fromTrade(trade))
            .market(MarketContext.fromTrade(trade, null, deliveryDate, null, null, null))
            .pricing(PricingContext.fromTrade(trade))
            .risk(RiskContext.fromTrade(trade))
            .accounting(AccountingContext.fromTrade(trade))
            .credit(CreditContext.fromTrade(trade))
            .audit(AuditContext.fromTrade(trade))
            .build();
        System.out.println("[PricingService] Valuation context built for trade ID: " + context);
        System.out.println("[PricingService] Using engine: " + engine.getClass().getSimpleName());

        // Price the trade using the engine
        ValuationResult result = engine.price(trade, instrument, context);

        // Return the MTM total from the valuation result
        return result.getMtmTotal();
    }

        
}
