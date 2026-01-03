package com.trading.ctrm.reference;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

@RestController
@RequestMapping("/api/reference-data/deal-template-metadata")
public class DealTemplateMetadataController {
    @GetMapping
    public Map<String, List<String>> getDealTemplateMetadata() {
        Map<String, List<String>> metadata = new HashMap<>();
        metadata.put("currency", Arrays.asList("USD", "EUR", "GBP", "INR"));
        metadata.put("pricingModel", Arrays.asList("POWER_FORWARD", "Black76", "DCF", "RENEWABLE_FORECAST"));
        metadata.put("unit", Arrays.asList("MWh", "MMBtu", "Therm", "BBL", "MT", "USD"));
        metadata.put("instrumentType", Arrays.asList("FORWARD", "OPTION", "PPA", "SWAP"));
        return metadata;
    }
}
