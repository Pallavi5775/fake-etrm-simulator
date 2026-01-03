package com.trading.ctrm.instrument;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InstrumentEntityHelper {
    private final CommodityRepository commodityRepository;

    @Autowired
    public InstrumentEntityHelper(CommodityRepository commodityRepository) {
        this.commodityRepository = commodityRepository;
    }

    public Commodity resolveCommodity(Long commodityId) {
        return commodityRepository.findById(commodityId)
                .orElseThrow(() -> new IllegalArgumentException("Commodity not found: " + commodityId));
    }
}
