package com.trading.ctrm.marketdata;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MarketDataService {

    private final MarketPriceRepository priceRepository;

    public MarketDataService(MarketPriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    public MarketDataSnapshot loadSnapshot() {

        Map<String, java.math.BigDecimal> prices =
                priceRepository.findAll()
                        .stream()
                        .collect(Collectors.toMap(
                                MarketPrice::getInstrumentCode,
                                MarketPrice::getPrice
                        ));

        return new MarketDataSnapshot(prices);
    }
}
