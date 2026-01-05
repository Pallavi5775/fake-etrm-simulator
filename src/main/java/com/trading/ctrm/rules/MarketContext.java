package com.trading.ctrm.rules;

import java.time.LocalDate;
import com.trading.ctrm.trade.Trade;


import java.math.BigDecimal;
import java.util.List;
import com.trading.ctrm.trade.ForwardCurveRepository;
import com.trading.ctrm.trade.ForwardCurve;
import com.trading.ctrm.instrument.Instrument;
import com.trading.ctrm.instrument.InstrumentType;
import com.trading.ctrm.yieldcurve.YieldCurveRepository;
import com.trading.ctrm.yieldcurve.YieldCurve;
import com.trading.ctrm.volatility.VolatilityRepository;
import com.trading.ctrm.volatility.Volatility;
import com.trading.ctrm.forecast.ForecastPriceRepository;
import com.trading.ctrm.forecast.ForecastPrice;
import com.trading.ctrm.marketdata.MarketPriceRepository;
import com.trading.ctrm.marketdata.MarketPrice;
import com.trading.ctrm.weather.WeatherDataRepository;
import com.trading.ctrm.weather.WeatherData;
import com.trading.ctrm.generation.GenerationForecastRepository;
import com.trading.ctrm.generation.GenerationForecast;
import com.trading.ctrm.pricing.VolatilitySurfaceRepository;
import com.trading.ctrm.pricing.VolatilitySurface;
import com.trading.ctrm.pricecurve.PriceCurveRepository;
import com.trading.ctrm.pricecurve.PriceCurve;
import com.trading.ctrm.pricing.OptionForwardCurveRepository;
import com.trading.ctrm.pricing.OptionForwardCurve;
import com.trading.ctrm.pricing.OptionVolatilityRepository;
import com.trading.ctrm.pricing.OptionVolatility;
import com.trading.ctrm.pricing.OptionYieldCurveRepository;
import com.trading.ctrm.pricing.OptionYieldCurve;
import com.trading.ctrm.pricing.MarketCurveRepository;
import com.trading.ctrm.pricing.MarketCurve;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class MarketContext {
        // Dummy implementations for demo; replace with real data lookup
        private ForwardCurveRepository forwardCurveRepository;
        private YieldCurveRepository yieldCurveRepository;
        private VolatilityRepository volatilityRepository;
        private ForecastPriceRepository forecastPriceRepository;
        private MarketPriceRepository marketPriceRepository;
        private WeatherDataRepository weatherDataRepository;
        private GenerationForecastRepository generationForecastRepository;
        private VolatilitySurfaceRepository volatilitySurfaceRepository;
        private PriceCurveRepository priceCurveRepository;
        private MarketCurveRepository marketCurveRepository;
        private OptionForwardCurveRepository optionForwardCurveRepository;
        private OptionVolatilityRepository optionVolatilityRepository;
        private OptionYieldCurveRepository optionYieldCurveRepository;

        // Market data context fields
        private String marketDataSet;   // EOD / INTRADAY
        private LocalDate pricingDate;
        private String curveSet;
        private String fxScenario;
        private String volatilitySurface;
        // Added for renewable forecast pricing
        private List<BigDecimal> forecastPrices;

        private static final Logger log = LoggerFactory.getLogger(MarketContext.class);

        @Autowired
        public MarketContext(
            ForwardCurveRepository forwardCurveRepository,
            YieldCurveRepository yieldCurveRepository,
            VolatilityRepository volatilityRepository,
            ForecastPriceRepository forecastPriceRepository,
            MarketPriceRepository marketPriceRepository,
            WeatherDataRepository weatherDataRepository,
            GenerationForecastRepository generationForecastRepository,
            VolatilitySurfaceRepository volatilitySurfaceRepository,
            PriceCurveRepository priceCurveRepository,
            MarketCurveRepository marketCurveRepository,
            OptionForwardCurveRepository optionForwardCurveRepository,
            OptionVolatilityRepository optionVolatilityRepository,
            OptionYieldCurveRepository optionYieldCurveRepository
        ) {
            this.forwardCurveRepository = forwardCurveRepository;
            this.yieldCurveRepository = yieldCurveRepository;
            this.volatilityRepository = volatilityRepository;
            this.forecastPriceRepository = forecastPriceRepository;
            this.marketPriceRepository = marketPriceRepository;
            this.weatherDataRepository = weatherDataRepository;
            this.generationForecastRepository = generationForecastRepository;
            this.volatilitySurfaceRepository = volatilitySurfaceRepository;
            this.priceCurveRepository = priceCurveRepository;
            this.marketCurveRepository = marketCurveRepository;
            this.optionForwardCurveRepository = optionForwardCurveRepository;
            this.optionVolatilityRepository = optionVolatilityRepository;
            this.optionYieldCurveRepository = optionYieldCurveRepository;
            this.marketDataSet = "EOD";
            this.pricingDate = java.time.LocalDate.now();
            this.curveSet = "USD_CURVES";
            this.fxScenario = "FX_EOD";
            this.volatilitySurface = null;
            this.forecastPrices = null;
        }

        public java.math.BigDecimal getYieldCurve(Instrument instrument, LocalDate date) {
        // For options, use separate option yield curve data
        if (instrument.getInstrumentType() == InstrumentType.OPTION) {
            if (optionYieldCurveRepository == null) return null;

            // For options, use currency-based yield curve
            String curveName = instrument.getCurrency() + "_DISCOUNT";
            log.info("[YieldCurveLookup] Looking up OPTION yield curve '{}' for instrument '{}', date='{}'", curveName, instrument.getInstrumentCode(), date);

            java.math.BigDecimal result = optionYieldCurveRepository.findByCurveNameAndDate(curveName, date)
                .map(OptionYieldCurve::getYield)
                .map(BigDecimal::valueOf)
                .orElse(null);

            if (result == null) {
                // Try fallback date (2024-12-05) for demo purposes
                LocalDate fallbackDate = LocalDate.of(2024, 12, 5);
                log.warn("[YieldCurveLookup] No option yield curve found for date '{}', trying fallback date '{}'", date, fallbackDate);
                result = optionYieldCurveRepository.findByCurveNameAndDate(curveName, fallbackDate)
                    .map(OptionYieldCurve::getYield)
                    .map(BigDecimal::valueOf)
                    .orElse(null);
            }

            if (result != null) {
                log.info("[YieldCurveLookup] Successfully retrieved option yield {} from curve '{}', date='{}'", result, curveName, date);
            } else {
                log.warn("[YieldCurveLookup] No option yield curve found for curve '{}', date='{}'", curveName, date);
            }
            return result;
        }

        // For non-options, use regular yield curve logic
        if (yieldCurveRepository == null) return null;

        // For derivatives, use currency-based yield curve
        // For physical forwards, could use instrument-specific curves if available
        String curveName;
        if (instrument.getInstrumentType() == InstrumentType.COMMODITY_SWAP ||
            instrument.getInstrumentType() == InstrumentType.GAS_FORWARD ||
            instrument.getInstrumentType() == InstrumentType.POWER_FORWARD) {
            // Use currency-based yield curve for derivatives
            curveName = instrument.getCurrency() + "_DISCOUNT";
        } else {
            // For other instruments, try instrument-specific curve first, fallback to currency
            curveName = instrument.getInstrumentCode();
            // If no instrument-specific curve found, we'll try currency-based below
        }

        log.info("[YieldCurveLookup] Looking up yield curve '{}' for instrument '{}', date='{}'", curveName, instrument.getInstrumentCode(), date);
        java.math.BigDecimal result = yieldCurveRepository.findByCurveNameAndDate(curveName, date)
            .map(YieldCurve::getYield)
            .map(BigDecimal::valueOf)
            .orElse(null);

        if (result == null && !curveName.endsWith("_DISCOUNT")) {
            // Fallback to currency-based curve for instruments without specific curves
            String currencyCurveName = instrument.getCurrency() + "_DISCOUNT";
            log.info("[YieldCurveLookup] No instrument-specific curve found, trying currency curve '{}'", currencyCurveName);
            result = yieldCurveRepository.findByCurveNameAndDate(currencyCurveName, date)
                .map(YieldCurve::getYield)
                .map(BigDecimal::valueOf)
                .orElse(null);
        }

        if (result == null) {
            // Try fallback date (2024-12-05) for demo purposes when current date has no data
            LocalDate fallbackDate = LocalDate.of(2024, 12, 5);
            log.warn("[YieldCurveLookup] No yield curve found for date '{}', trying fallback date '{}'", date, fallbackDate);

            // Try original curve name with fallback date
            result = yieldCurveRepository.findByCurveNameAndDate(curveName, fallbackDate)
                .map(YieldCurve::getYield)
                .map(BigDecimal::valueOf)
                .orElse(null);

            if (result == null && !curveName.endsWith("_DISCOUNT")) {
                // Try currency curve with fallback date
                String currencyCurveName = instrument.getCurrency() + "_DISCOUNT";
                result = yieldCurveRepository.findByCurveNameAndDate(currencyCurveName, fallbackDate)
                    .map(YieldCurve::getYield)
                    .map(BigDecimal::valueOf)
                    .orElse(null);
            }
        }

        if (result == null) {
            log.error("[YieldCurveLookup] No yield curve found for curveName='{}' or currency fallback, date='{}' or fallback date", curveName, date);
        } else {
            log.info("[YieldCurveLookup] Successfully retrieved yield {} from curve '{}', date='{}'", result, curveName, date);
        }
        return result;
    }

        public java.math.BigDecimal getForwardCurve(Instrument instrument, LocalDate date) {
                log.info("[ForwardCurveLookup] Starting forward curve lookup for instrument '{}' (type: {}), date: '{}'",
                        instrument.getInstrumentCode(), instrument.getInstrumentType(), date);

                if (forwardCurveRepository == null) {
                    log.warn("[ForwardCurveLookup] ForwardCurveRepository is null, returning null");
                    return null;
                }

                // For options, use separate option forward curve data
                if (instrument.getInstrumentType() == InstrumentType.OPTION) {
                    log.info("[ForwardCurveLookup] Using OPTION forward curve data for '{}'", instrument.getInstrumentCode());

                    // For options, determine the appropriate date for forward curve lookup
                    LocalDate lookupDate = date;
                    if (instrument instanceof com.trading.ctrm.instrument.CommodityOptionInstrument) {
                        com.trading.ctrm.instrument.CommodityOptionInstrument option =
                            (com.trading.ctrm.instrument.CommodityOptionInstrument) instrument;
                        if (option.getExpiryDate() != null) {
                            lookupDate = option.getExpiryDate();
                            log.info("[ForwardCurveLookup] Using option expiry date: {} instead of {}", lookupDate, date);
                        }
                    }

                    // Use option forward curve repository
                    java.math.BigDecimal result = optionForwardCurveRepository.findByInstrumentCodeAndDeliveryDate(
                            instrument.getInstrumentCode(), lookupDate)
                        .map(OptionForwardCurve::getForwardPrice)
                        .map(BigDecimal::valueOf)
                        .orElse(null);

                    if (result != null) {
                        log.info("[ForwardCurveLookup] SUCCESS: Found option forward curve value {} for '{}', date '{}'",
                                result, instrument.getInstrumentCode(), lookupDate);
                        return result;
                    } else {
                        log.warn("[ForwardCurveLookup] No option forward curve found for '{}', date '{}'",
                                instrument.getInstrumentCode(), lookupDate);
                        return null;
                    }
                }

                // For non-options, use regular forward curve lookup
                final String lookupInstrumentCode = mapInstrumentCodeToDatabaseFormat(instrument.getInstrumentCode());
                log.info("[ForwardCurveLookup] Using mapped instrument code: '{}' (original: '{}')",
                        lookupInstrumentCode, instrument.getInstrumentCode());

                log.info("[ForwardCurveLookup] Attempting initial lookup for instrument '{}', date '{}'", lookupInstrumentCode, date);
                
                // DEBUG: Check what data exists for this instrument code
                List<ForwardCurve> allForInstrument = forwardCurveRepository.findByInstrumentCode(lookupInstrumentCode);
                System.out.println("=== FORWARD CURVE DEBUG ===");
                System.out.println("Looking for instrument: '" + lookupInstrumentCode + "', date: '" + date + "'");
                System.out.println("Found " + allForInstrument.size() + " curves for instrument '" + lookupInstrumentCode + "'");
                log.info("[ForwardCurveLookup] Found {} total forward curves for instrument '{}'", allForInstrument.size(), lookupInstrumentCode);
                
                List<ForwardCurve> allForDate = forwardCurveRepository.findByDeliveryDate(date);
                System.out.println("Found " + allForDate.size() + " curves for date '" + date + "'");
                log.info("[ForwardCurveLookup] Found {} total forward curves for date '{}'", allForDate.size(), date);
                
                List<ForwardCurve> exactMatches = forwardCurveRepository.findAllByInstrumentCodeAndDeliveryDate(lookupInstrumentCode, date);
                System.out.println("Found " + exactMatches.size() + " EXACT matches for instrument + date");
                log.info("[ForwardCurveLookup] Found {} exact matches for instrument '{}' and date '{}'", exactMatches.size(), lookupInstrumentCode, date);
                
                if (!exactMatches.isEmpty()) {
                    System.out.println("EXACT MATCH DETAILS:");
                    exactMatches.forEach(fc -> {
                        System.out.println("  ID: " + fc.getId() + ", Instrument: " + fc.getInstrument().getInstrumentCode() + 
                                         ", DeliveryDate: " + fc.getDeliveryDate() + ", Price: " + fc.getPrice() + 
                                         ", CurveDate: " + fc.getCurveDate());
                        log.info("[ForwardCurveLookup]   ID: {}, Instrument: {}, DeliveryDate: {}, Price: {}, CurveDate: {}", 
                                 fc.getId(), fc.getInstrument().getInstrumentCode(), fc.getDeliveryDate(), fc.getPrice(), fc.getCurveDate());
                    });
                } else {
                    System.out.println("NO EXACT MATCHES FOUND!");
                }
                
                // Show all instruments in database for reference
                List<String> allInstruments = forwardCurveRepository.findDistinctInstruments();
                System.out.println("All instruments in forward_curves table: " + allInstruments);
                
                java.math.BigDecimal result = forwardCurveRepository.findByInstrumentCodeAndDeliveryDate(lookupInstrumentCode, date)
                    .map(ForwardCurve::getPrice)
                    .map(BigDecimal::valueOf)
                    .orElse(null);

                System.out.println("MAIN QUERY RESULT: " + (result != null ? "FOUND (" + result + ")" : "NULL"));
                System.out.println("=== END DEBUG ===");

                if (result != null) {
                    log.info("[ForwardCurveLookup] SUCCESS: Found forward curve value {} for '{}', date '{}'", result, lookupInstrumentCode, date);
                    return result;
                }

                log.warn("[ForwardCurveLookup] Initial lookup failed for '{}', date '{}'", lookupInstrumentCode, date);

                if (instrument.getInstrumentType() == InstrumentType.OPTION) {
                    log.info("[ForwardCurveLookup] Attempting quarterly option date adjustment for '{}'", instrument.getInstrumentCode());

                    // For options, try to map quarterly contracts to monthly delivery dates
                    String optionCode = instrument.getInstrumentCode();
                    LocalDate adjustedDate = date;

                    if (optionCode.contains("_Q1_")) {
                        // Q1 options use January delivery
                        adjustedDate = LocalDate.of(date.getYear(), 1, 1);
                        log.info("[ForwardCurveLookup] Q1 option: adjusting date from {} to {}", date, adjustedDate);
                    } else if (optionCode.contains("_Q2_")) {
                        // Q2 options use April delivery
                        adjustedDate = LocalDate.of(date.getYear(), 4, 1);
                        log.info("[ForwardCurveLookup] Q2 option: adjusting date from {} to {}", date, adjustedDate);
                    } else if (optionCode.contains("_Q3_")) {
                        // Q3 options use July delivery
                        adjustedDate = LocalDate.of(date.getYear(), 7, 1);
                        log.info("[ForwardCurveLookup] Q3 option: adjusting date from {} to {}", date, adjustedDate);
                    } else if (optionCode.contains("_Q4_")) {
                        // Q4 options use October delivery
                        adjustedDate = LocalDate.of(date.getYear(), 10, 1);
                        log.info("[ForwardCurveLookup] Q4 option: adjusting date from {} to {}", date, adjustedDate);
                    }

                    if (!adjustedDate.equals(date)) {
                        System.out.println("Trying quarterly option adjusted date: " + adjustedDate + " (original: " + date + ")");
                        log.info("[ForwardCurveLookup] For quarterly option, trying adjusted date '{}' instead of '{}'", adjustedDate, date);
                        result = forwardCurveRepository.findByInstrumentCodeAndDeliveryDate(lookupInstrumentCode, adjustedDate)
                            .map(ForwardCurve::getPrice)
                            .map(BigDecimal::valueOf)
                            .orElse(null);

                        System.out.println("Quarterly adjusted date result: " + (result != null ? result : "NULL"));
                        if (result != null) {
                            log.info("[ForwardCurveLookup] SUCCESS: Found forward curve value {} using adjusted date '{}' for quarterly option", result, adjustedDate);
                            return result;
                        }

                        log.warn("[ForwardCurveLookup] Quarterly option date adjustment failed for adjusted date '{}'", adjustedDate);
                    } else {
                        System.out.println("No quarterly adjustment needed");
                        log.info("[ForwardCurveLookup] No quarterly adjustment needed for option '{}'", optionCode);
                    }
                }

                // Fallback: try to find the closest available delivery date for the same month/year
                System.out.println("Trying general fallback logic");
                log.info("[ForwardCurveLookup] Attempting fallback lookup for '{}', original date '{}'", lookupInstrumentCode, date);

                // For demo purposes, try the first day of the month if the exact date isn't found
                LocalDate fallbackDate = date.withDayOfMonth(1);
                if (!fallbackDate.equals(date)) {
                    System.out.println("Trying fallback date: " + fallbackDate + " (first day of month)");
                    log.info("[ForwardCurveLookup] Trying fallback date: {} (first day of month)", fallbackDate);
                    result = forwardCurveRepository.findByInstrumentCodeAndDeliveryDate(lookupInstrumentCode, fallbackDate)
                        .map(ForwardCurve::getPrice)
                        .map(BigDecimal::valueOf)
                        .orElse(null);

                    System.out.println("General fallback result: " + (result != null ? result : "NULL"));
                    if (result != null) {
                        log.info("[ForwardCurveLookup] SUCCESS: Found forward curve value {} using fallback date '{}' for '{}'", result, fallbackDate, lookupInstrumentCode);
                        return result;
                    }

                    log.warn("[ForwardCurveLookup] Fallback date lookup failed for '{}', date '{}'", lookupInstrumentCode, fallbackDate);
                } else {
                    System.out.println("Date is already first day of month, no general fallback needed");
                    log.info("[ForwardCurveLookup] Date '{}' is already first day of month, no fallback needed", date);
                }

        return result;
        }

        // Instrument code mapping to match database format
        private String mapInstrumentCodeToDatabaseFormat(String applicationCode) {
            // Define mappings from application codes to database codes
            // Add mappings as needed based on your database format
            switch (applicationCode) {
                case "POWER_BASELOAD_2025":
                    return "PWR-BASELOAD-2025";
                case "POWER_BASELOAD_2026":
                    return "PWR-BASELOAD-2026";
                case "POWER_BASELOAD_FORWARD_2025":
                    return "PWR-BASELOAD-2025";
                case "POWER_BASELOAD_FORWARD_2026":
                    return "PWR-BASELOAD-2026";
                // Add more mappings as needed
                default:
                    // For unknown codes, try some generic transformations
                    String mapped = applicationCode
                        .replace("POWER_BASELOAD_", "PWR-BASELOAD-")
                        .replace("POWER_BASELOAD_FORWARD_", "PWR-BASELOAD-")
                        .replace("_", "-");

                    log.debug("[InstrumentMapping] Applied generic transformation: '{}' -> '{}'",
                             applicationCode, mapped);
                    return mapped;
            }
        }

        public java.math.BigDecimal getVolatility(Instrument instrument, LocalDate date) {
                // For options, use separate option volatility data
                if (instrument.getInstrumentType() == InstrumentType.OPTION) {
                    if (optionVolatilityRepository == null) return null;
                    return optionVolatilityRepository.findByInstrumentCodeAndDate(instrument.getInstrumentCode(), date)
                        .map(OptionVolatility::getValue)
                        .map(BigDecimal::valueOf)
                        .orElse(null);
                }

                // For non-options, use regular volatility data
                if (volatilityRepository == null) return null;
                return volatilityRepository.findByInstrumentAndDate(instrument, date)
                    .map(Volatility::getValue)
                    .map(BigDecimal::valueOf)
                    .orElse(null);
        }

        public java.math.BigDecimal getForecastPrice(Instrument instrument, LocalDate date) {
                if (forecastPriceRepository == null) return null;
                return forecastPriceRepository.findByInstrumentAndDate(instrument, date)
                    .map(ForecastPrice::getValue)
                    .map(BigDecimal::valueOf)
                    .orElse(null);
        }

        public java.math.BigDecimal getMarketPrice(Instrument instrument, LocalDate date) {
                if (marketPriceRepository == null) return null;
                return marketPriceRepository.findByInstrumentCode(instrument.getInstrumentCode())
                    .map(MarketPrice::getPrice)
                    .orElse(null);
        }

        public WeatherData getWeatherData(String location, LocalDate date) {
                if (weatherDataRepository == null) return null;
                // Assuming WeatherData has a method to find by location and date
                return weatherDataRepository.findByLocationAndDate(location, date).orElse(null);
        }

        public GenerationForecast getGenerationForecast(Instrument instrument, LocalDate date) {
                if (generationForecastRepository == null) return null;
                // Assuming plant name matches instrument code
                return generationForecastRepository.findByPlantNameAndDate(instrument.getInstrumentCode(), date).orElse(null);
        }

        public VolatilitySurface getVolatilitySurface(String underlying, LocalDate date, String surfaceType) {
                if (volatilitySurfaceRepository == null) return null;
                return volatilitySurfaceRepository.findByUnderlyingAndPricingDateAndType(underlying, date, surfaceType).orElse(null);
        }

        public java.math.BigDecimal getPriceCurveValue(Instrument instrument, LocalDate date) {
                if (priceCurveRepository == null) return null;
                // Assuming curve name matches instrument code
                return priceCurveRepository.findByCurveNameAndDate(instrument.getInstrumentCode(), date)
                    .map(PriceCurve::getPrice)
                    .map(BigDecimal::valueOf)
                    .orElse(null);
        }

        public MarketCurve getMarketCurve(String curveName, LocalDate date) {
                if (marketCurveRepository == null) return null;
                // Assuming MarketCurve has a method to find by name and date
                return marketCurveRepository.findByCurveNameAndDate(curveName, date).orElse(null);
        }

        // Constructor that takes repositories for copying
        public MarketContext(
                String marketDataSet,
                LocalDate pricingDate,
                String curveSet,
                String fxScenario,
                String volatilitySurface,
                List<BigDecimal> forecastPrices,
                ForwardCurveRepository forwardCurveRepository,
                YieldCurveRepository yieldCurveRepository,
                VolatilityRepository volatilityRepository,
                ForecastPriceRepository forecastPriceRepository,
                MarketPriceRepository marketPriceRepository,
                WeatherDataRepository weatherDataRepository,
                GenerationForecastRepository generationForecastRepository,
                VolatilitySurfaceRepository volatilitySurfaceRepository,
                PriceCurveRepository priceCurveRepository,
                MarketCurveRepository marketCurveRepository
        ) {
            this.marketDataSet = marketDataSet;
            this.pricingDate = pricingDate;
            this.curveSet = curveSet;
            this.fxScenario = fxScenario;
            this.volatilitySurface = volatilitySurface;
            this.forecastPrices = forecastPrices;
            this.forwardCurveRepository = forwardCurveRepository;
            this.yieldCurveRepository = yieldCurveRepository;
            this.volatilityRepository = volatilityRepository;
            this.forecastPriceRepository = forecastPriceRepository;
            this.marketPriceRepository = marketPriceRepository;
            this.weatherDataRepository = weatherDataRepository;
            this.generationForecastRepository = generationForecastRepository;
            this.volatilitySurfaceRepository = volatilitySurfaceRepository;
            this.priceCurveRepository = priceCurveRepository;
            this.marketCurveRepository = marketCurveRepository;
        }

        // Simple constructor for factory methods
        public MarketContext(
                String marketDataSet,
                LocalDate pricingDate,
                String curveSet,
                String fxScenario,
                String volatilitySurface
        ) {
            this.marketDataSet = marketDataSet;
            this.pricingDate = pricingDate;
            this.curveSet = curveSet;
            this.fxScenario = fxScenario;
            this.volatilitySurface = volatilitySurface;
            this.forecastPrices = null;
            this.forwardCurveRepository = null;
            this.yieldCurveRepository = null;
            this.volatilityRepository = null;
            this.forecastPriceRepository = null;
            this.marketPriceRepository = null;
            this.weatherDataRepository = null;
            this.generationForecastRepository = null;
            this.volatilitySurfaceRepository = null;
            this.priceCurveRepository = null;
            this.marketCurveRepository = null;
        }

        // Constructor with forecast prices
        public MarketContext(
                String marketDataSet,
                LocalDate pricingDate,
                String curveSet,
                String fxScenario,
                String volatilitySurface,
                List<BigDecimal> forecastPrices
        ) {
            this.marketDataSet = marketDataSet;
            this.pricingDate = pricingDate;
            this.curveSet = curveSet;
            this.fxScenario = fxScenario;
            this.volatilitySurface = volatilitySurface;
            this.forecastPrices = forecastPrices;
            this.forwardCurveRepository = null;
            this.yieldCurveRepository = null;
            this.volatilityRepository = null;
            this.forecastPriceRepository = null;
            this.marketPriceRepository = null;
            this.weatherDataRepository = null;
            this.generationForecastRepository = null;
            this.volatilitySurfaceRepository = null;
            this.priceCurveRepository = null;
            this.marketCurveRepository = null;
        }
        public LocalDate pricingDate() { return pricingDate; }
        public void setPricingDate(LocalDate pricingDate) { this.pricingDate = pricingDate; }
        public String marketDataSet() { return marketDataSet; }
        public String curveSet() { return curveSet; }
        public String fxScenario() { return fxScenario; }
        public String volatilitySurface() { return volatilitySurface; }
        public List<BigDecimal> forecastPrices() { return forecastPrices; }

    public static MarketContext of(
            String marketDataSet,
            LocalDate pricingDate,
            String curveSet,
            String fxScenario,
            String volatilitySurface
    ) {
        return new MarketContext(
                marketDataSet,
                pricingDate,
                curveSet,
                fxScenario,
                volatilitySurface
        );
    }

    public static MarketContext of(
            String marketDataSet,
            LocalDate pricingDate,
            String curveSet,
            String fxScenario,
            String volatilitySurface,
            List<BigDecimal> forecastPrices
    ) {
        return new MarketContext(
                marketDataSet,
                pricingDate,
                curveSet,
                fxScenario,
                volatilitySurface,
                forecastPrices
        );
    }

    /**
     * Factory method to create MarketContext from a Trade entity with default market data settings
     */
    public static MarketContext fromTrade(Trade trade) {
        // This method is problematic in service layer. Use injected MarketContext instead.
        // For now, return a basic instance - the calling service should use injected MarketContext
        return new MarketContext("EOD", LocalDate.now(), "USD_CURVES", "FX_EOD", null);
    }

    /**
     * Factory method with configurable parameters from UI
     */
    public static MarketContext fromTrade(
            Trade trade,
            String marketDataSet,
            LocalDate pricingDate,
            String curveSet,
            String fxScenario,
            String volatilitySurface
    ) {
        return new MarketContext(
                marketDataSet != null ? marketDataSet : "EOD",
                pricingDate != null ? pricingDate : LocalDate.now(),
                curveSet != null ? curveSet : "USD_CURVES",
                fxScenario != null ? fxScenario : "FX_EOD",
                volatilitySurface != null ? volatilitySurface : trade.getInstrument().getInstrumentType().name() + "_VOL"
        );
    }

    // Getter methods for repositories to allow copying in TradeService
    public ForwardCurveRepository getForwardCurveRepository() { return forwardCurveRepository; }
    public YieldCurveRepository getYieldCurveRepository() { return yieldCurveRepository; }
    public VolatilityRepository getVolatilityRepository() { return volatilityRepository; }
    public ForecastPriceRepository getForecastPriceRepository() { return forecastPriceRepository; }
    public MarketPriceRepository getMarketPriceRepository() { return marketPriceRepository; }
    public WeatherDataRepository getWeatherDataRepository() { return weatherDataRepository; }
    public GenerationForecastRepository getGenerationForecastRepository() { return generationForecastRepository; }
    public VolatilitySurfaceRepository getVolatilitySurfaceRepository() { return volatilitySurfaceRepository; }
    public PriceCurveRepository getPriceCurveRepository() { return priceCurveRepository; }
    public MarketCurveRepository getMarketCurveRepository() { return marketCurveRepository; }
    public OptionForwardCurveRepository getOptionForwardCurveRepository() { return optionForwardCurveRepository; }
    public OptionVolatilityRepository getOptionVolatilityRepository() { return optionVolatilityRepository; }
    public OptionYieldCurveRepository getOptionYieldCurveRepository() { return optionYieldCurveRepository; }
}