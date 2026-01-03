package com.trading.ctrm.instrument;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commodities")
public class CommodityController {

    private final CommodityRepository commodityRepository;

    @Autowired
    public CommodityController(CommodityRepository commodityRepository) {
        this.commodityRepository = commodityRepository;
    }

    @GetMapping
    public List<Commodity> getAllCommodities() {
        return commodityRepository.findAll();
    }

    @PostMapping
    public Commodity createCommodity(@RequestBody Commodity commodity) {
        return commodityRepository.save(commodity);
    }

    @PutMapping("/{id}")
    public Commodity updateCommodity(@PathVariable Long id, @RequestBody Commodity commodity) {
        return commodityRepository.findById(id)
                .map(existing -> {
                    existing.setName(commodity.getName());
                    return commodityRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Commodity not found: " + id));
    }

    @DeleteMapping("/{id}")
    public void deleteCommodity(@PathVariable Long id) {
        commodityRepository.deleteById(id);
    }
}
