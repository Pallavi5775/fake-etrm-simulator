package com.trading.ctrm.pricing;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * Options Market Data Template Loader
 * Populates sample data for futures and forwards options
 */
@Component
public class OptionsMarketDataLoader {

    @Autowired
    private OptionForwardCurveRepository optionForwardCurveRepository;

    @Autowired
    private OptionVolatilityRepository optionVolatilityRepository;

    @Autowired
    private OptionYieldCurveRepository optionYieldCurveRepository;

    // Add repositories for instruments and trades
    @Autowired
    private com.trading.ctrm.trade.InstrumentRepository instrumentRepository;

    @Autowired
    private com.trading.ctrm.trade.TradeRepository tradeRepository;

    @Autowired
    private com.trading.ctrm.instrument.CommodityRepository commodityRepository;

    @Transactional
    public void loadSampleData() {
        loadCommodities();
        loadInstruments();
        loadTrades();
        loadForwardCurves();
        loadVolatilityData();
        loadYieldCurves();
    }

    private void loadCommodities() {
        createCommodityIfNotExists("WTI Crude Oil");
        createCommodityIfNotExists("Brent Crude Oil");
        createCommodityIfNotExists("Natural Gas");
        createCommodityIfNotExists("Power Baseload");
        createCommodityIfNotExists("Gold");
        createCommodityIfNotExists("Copper");
        createCommodityIfNotExists("Corn");
        createCommodityIfNotExists("Soybeans");
    }

    private void loadInstruments() {
        LocalDate yearEnd = LocalDate.of(2026, 12, 31);
        LocalDate q1End = LocalDate.of(2026, 3, 31);

        // Energy - Oil Futures Options
        createOptionInstrument("WTI_FUTURES_CALL_2026", "WTI Crude Oil", "USD", "BBL", "FUTURES", "CALL", new BigDecimal("80.00"), yearEnd);
        createOptionInstrument("WTI_FUTURES_PUT_2026", "WTI Crude Oil", "USD", "BBL", "FUTURES", "PUT", new BigDecimal("80.00"), yearEnd);
        createOptionInstrument("BRENT_FUTURES_CALL_2026", "Brent Crude Oil", "USD", "BBL", "FUTURES", "CALL", new BigDecimal("82.00"), yearEnd);
        createOptionInstrument("BRENT_FUTURES_PUT_2026", "Brent Crude Oil", "USD", "BBL", "FUTURES", "PUT", new BigDecimal("82.00"), yearEnd);

        // Energy - Gas Futures Options
        createOptionInstrument("NG_FUTURES_CALL_Q1_2026", "Natural Gas", "USD", "MMBTU", "FUTURES", "CALL", new BigDecimal("3.50"), q1End);
        createOptionInstrument("NG_FUTURES_PUT_Q1_2026", "Natural Gas", "USD", "MMBTU", "FUTURES", "PUT", new BigDecimal("3.50"), q1End);

        // Energy - Power Forward Options (OTC)
        createOptionInstrument("POWER_BASELOAD_FORWARD_CALL_2026", "Power Baseload", "USD", "MWH", "FORWARD", "CALL", new BigDecimal("50.00"), yearEnd);
        createOptionInstrument("POWER_BASELOAD_FORWARD_PUT_2026", "Power Baseload", "USD", "MWH", "FORWARD", "PUT", new BigDecimal("50.00"), yearEnd);

        // Metals - Futures Options
        createOptionInstrument("GOLD_FUTURES_CALL_2026", "Gold", "USD", "OZ", "FUTURES", "CALL", new BigDecimal("1900.00"), yearEnd);
        createOptionInstrument("GOLD_FUTURES_PUT_2026", "Gold", "USD", "OZ", "FUTURES", "PUT", new BigDecimal("1900.00"), yearEnd);
        createOptionInstrument("COPPER_FUTURES_CALL_2026", "Copper", "USD", "LB", "FUTURES", "CALL", new BigDecimal("4.00"), yearEnd);
        createOptionInstrument("COPPER_FUTURES_PUT_2026", "Copper", "USD", "LB", "FUTURES", "PUT", new BigDecimal("4.00"), yearEnd);

        // Agriculture - Futures Options
        createOptionInstrument("CORN_FUTURES_CALL_2026", "Corn", "USD", "BU", "FUTURES", "CALL", new BigDecimal("5.00"), yearEnd);
        createOptionInstrument("CORN_FUTURES_PUT_2026", "Corn", "USD", "BU", "FUTURES", "PUT", new BigDecimal("5.00"), yearEnd);
        createOptionInstrument("SOYBEAN_FUTURES_CALL_2026", "Soybeans", "USD", "BU", "FUTURES", "CALL", new BigDecimal("13.00"), yearEnd);
        createOptionInstrument("SOYBEAN_FUTURES_PUT_2026", "Soybeans", "USD", "BU", "FUTURES", "PUT", new BigDecimal("13.00"), yearEnd);
    }

    private void loadTrades() {
        LocalDate today = LocalDate.of(2026, 1, 5);

        // Sample option trades
        createOptionTrade("OPT_WTI_CALL_001", "WTI_FUTURES_CALL_2026", "Morgan Stanley", "ENERGY_TRADING", new BigDecimal("1000.00"), new BigDecimal("80.00"), "BUY", today);
        createOptionTrade("OPT_WTI_PUT_001", "WTI_FUTURES_PUT_2026", "Goldman Sachs", "ENERGY_HEDGE", new BigDecimal("1000.00"), new BigDecimal("80.00"), "SELL", today);

        createOptionTrade("OPT_BRENT_CALL_001", "BRENT_FUTURES_CALL_2026", "JPMorgan", "CRUDE_TRADING", new BigDecimal("500.00"), new BigDecimal("82.00"), "BUY", today);
        createOptionTrade("OPT_BRENT_PUT_001", "BRENT_FUTURES_PUT_2026", "Citibank", "CRUDE_HEDGE", new BigDecimal("500.00"), new BigDecimal("82.00"), "SELL", today);

        createOptionTrade("OPT_NG_CALL_001", "NG_FUTURES_CALL_Q1_2026", "Barclays", "GAS_TRADING", new BigDecimal("5000.00"), new BigDecimal("3.50"), "BUY", today);
        createOptionTrade("OPT_NG_PUT_001", "NG_FUTURES_PUT_Q1_2026", "Deutsche Bank", "GAS_HEDGE", new BigDecimal("5000.00"), new BigDecimal("3.50"), "SELL", today);

        createOptionTrade("OPT_POWER_CALL_001", "POWER_BASELOAD_FORWARD_CALL_2026", "EDF Trading", "POWER_TRADING", new BigDecimal("10000.00"), new BigDecimal("50.00"), "BUY", today);
        createOptionTrade("OPT_POWER_PUT_001", "POWER_BASELOAD_FORWARD_PUT_2026", "RWE Supply", "POWER_HEDGE", new BigDecimal("10000.00"), new BigDecimal("50.00"), "SELL", today);

        createOptionTrade("OPT_GOLD_CALL_001", "GOLD_FUTURES_CALL_2026", "UBS", "METALS_TRADING", new BigDecimal("100.00"), new BigDecimal("1900.00"), "BUY", today);
        createOptionTrade("OPT_GOLD_PUT_001", "GOLD_FUTURES_PUT_2026", "Credit Suisse", "METALS_HEDGE", new BigDecimal("100.00"), new BigDecimal("1900.00"), "SELL", today);

        createOptionTrade("OPT_COPPER_CALL_001", "COPPER_FUTURES_CALL_2026", "Societe Generale", "METALS_TRADING", new BigDecimal("500.00"), new BigDecimal("4.00"), "BUY", today);
        createOptionTrade("OPT_COPPER_PUT_001", "COPPER_FUTURES_PUT_2026", "BNP Paribas", "METALS_HEDGE", new BigDecimal("500.00"), new BigDecimal("4.00"), "SELL", today);

        createOptionTrade("OPT_CORN_CALL_001", "CORN_FUTURES_CALL_2026", "Cargill", "AGRI_TRADING", new BigDecimal("5000.00"), new BigDecimal("5.00"), "BUY", today);
        createOptionTrade("OPT_CORN_PUT_001", "CORN_FUTURES_PUT_2026", "ADM", "AGRI_HEDGE", new BigDecimal("5000.00"), new BigDecimal("5.00"), "SELL", today);

        createOptionTrade("OPT_SOYBEAN_CALL_001", "SOYBEAN_FUTURES_CALL_2026", "Bunge", "AGRI_TRADING", new BigDecimal("1000.00"), new BigDecimal("13.00"), "BUY", today);
        createOptionTrade("OPT_SOYBEAN_PUT_001", "SOYBEAN_FUTURES_PUT_2026", "Louis Dreyfus", "AGRI_HEDGE", new BigDecimal("1000.00"), new BigDecimal("13.00"), "SELL", today);
    }

    private void loadForwardCurves() {
        // Forward curves for each commodity
        createForwardCurve("WTI Crude Oil", LocalDate.of(2026, 12, 31), new BigDecimal("75.00"));
        createForwardCurve("Brent Crude Oil", LocalDate.of(2026, 12, 31), new BigDecimal("77.00"));
        createForwardCurve("Natural Gas", LocalDate.of(2026, 3, 31), new BigDecimal("3.20"));
        createForwardCurve("Power Baseload", LocalDate.of(2026, 12, 31), new BigDecimal("45.00"));
        createForwardCurve("Gold", LocalDate.of(2026, 12, 31), new BigDecimal("1850.00"));
        createForwardCurve("Copper", LocalDate.of(2026, 12, 31), new BigDecimal("3.80"));
        createForwardCurve("Corn", LocalDate.of(2026, 12, 31), new BigDecimal("4.80"));
        createForwardCurve("Soybeans", LocalDate.of(2026, 12, 31), new BigDecimal("12.50"));
    }

    private void loadVolatilityData() {
        // Volatility data for each commodity
        createVolatility("WTI Crude Oil", new BigDecimal("0.25"));
        createVolatility("Brent Crude Oil", new BigDecimal("0.28"));
        createVolatility("Natural Gas", new BigDecimal("0.35"));
        createVolatility("Power Baseload", new BigDecimal("0.30"));
        createVolatility("Gold", new BigDecimal("0.20"));
        createVolatility("Copper", new BigDecimal("0.22"));
        createVolatility("Corn", new BigDecimal("0.18"));
        createVolatility("Soybeans", new BigDecimal("0.16"));
    }

    private void loadYieldCurves() {
        // Yield curves for each commodity
        createYieldCurve("WTI Crude Oil", new BigDecimal("0.025"));
        createYieldCurve("Brent Crude Oil", new BigDecimal("0.025"));
        createYieldCurve("Natural Gas", new BigDecimal("0.030"));
        createYieldCurve("Power Baseload", new BigDecimal("0.035"));
        createYieldCurve("Gold", new BigDecimal("0.020"));
        createYieldCurve("Copper", new BigDecimal("0.025"));
        createYieldCurve("Corn", new BigDecimal("0.015"));
        createYieldCurve("Soybeans", new BigDecimal("0.015"));
    }

    // Helper methods for creating entities
    private void createCommodityIfNotExists(String name) {
        com.trading.ctrm.instrument.Commodity existing = commodityRepository.findByName(name);
        if (existing == null) {
            com.trading.ctrm.instrument.Commodity commodity = new com.trading.ctrm.instrument.Commodity();
            commodity.setName(name);
            commodityRepository.save(commodity);
        }
    }

    private void createOptionInstrument(String instrumentId, String commodityName, String currency, String unit,
                                      String underlyingType, String optionType, BigDecimal strikePrice, LocalDate expiryDate) {
        com.trading.ctrm.instrument.Commodity commodity = commodityRepository.findByName(commodityName);
        if (commodity != null) {
            com.trading.ctrm.instrument.CommodityOptionInstrument instrument = new com.trading.ctrm.instrument.CommodityOptionInstrument();
            instrument.setInstrumentCode(instrumentId);
            instrument.setCommodity(commodity);
            instrument.setCurrency(currency);
            instrument.setUnit(unit);
            instrument.setUnderlyingType(underlyingType);
            instrument.setOptionType(optionType);
            instrument.setStrikePrice(strikePrice);
            instrument.setExpiryDate(expiryDate);
            instrumentRepository.save(instrument);
        }
    }

    private void createOptionTrade(String tradeId, String instrumentId, String counterparty, String portfolio,
                                 BigDecimal quantity, BigDecimal price, String buySell, LocalDate tradeDate) {
        Optional<com.trading.ctrm.instrument.Instrument> instrumentOpt = instrumentRepository.findOptionalByInstrumentCode(instrumentId);
        if (instrumentOpt.isPresent()) {
            com.trading.ctrm.trade.Trade trade = com.trading.ctrm.trade.Trade.create();
            trade.setTradeId(tradeId);
            trade.setInstrument(instrumentOpt.get());
            trade.setCounterparty(counterparty);
            trade.setPortfolio(portfolio);
            trade.setQuantity(quantity);
            trade.setPrice(price);
            trade.setBuySell(com.trading.ctrm.trade.EnumType.BuySell.valueOf(buySell));
            trade.setTradeDate(tradeDate);
            tradeRepository.save(trade);
        }
    }

    private void createForwardCurve(String commodityName, LocalDate expiryDate, BigDecimal forwardPrice) {
        com.trading.ctrm.instrument.Commodity commodity = commodityRepository.findByName(commodityName);
        if (commodity != null) {
            OptionForwardCurve curve = new OptionForwardCurve();
            curve.setInstrumentCode(commodityName);
            curve.setDeliveryDate(expiryDate);
            curve.setForwardPrice(forwardPrice.doubleValue());
            optionForwardCurveRepository.save(curve);
        }
    }

    private void createVolatility(String commodityName, BigDecimal volatility) {
        com.trading.ctrm.instrument.Commodity commodity = commodityRepository.findByName(commodityName);
        if (commodity != null) {
            OptionVolatility vol = new OptionVolatility();
            vol.setInstrumentCode(commodityName);
            vol.setDate(java.time.LocalDate.now());
            vol.setValue(volatility.doubleValue());
            optionVolatilityRepository.save(vol);
        }
    }

    private void createYieldCurve(String commodityName, BigDecimal yieldRate) {
        com.trading.ctrm.instrument.Commodity commodity = commodityRepository.findByName(commodityName);
        if (commodity != null) {
            OptionYieldCurve yield = new OptionYieldCurve();
            yield.setCurveName(commodityName);
            yield.setDate(java.time.LocalDate.now());
            yield.setYield(yieldRate.doubleValue());
            optionYieldCurveRepository.save(yield);
        }
    }
}