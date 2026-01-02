package com.trading.ctrm.trade;

import org.springframework.web.bind.annotation.*;

import com.trading.ctrm.common.PortfolioPosition;
import com.trading.ctrm.trade.dto.PositionResponseDto;

import java.util.List;

@RestController
@RequestMapping("/api/positions")
public class PositionController {

    private final PortfolioPositionRepository repo;

    public PositionController(PortfolioPositionRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<PositionResponseDto> getPositions() {
        return repo.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    private PositionResponseDto toDto(PortfolioPosition p) {
        return new PositionResponseDto(
                p.getPortfolio(),
                p.getInstrument().getInstrumentCode(),
                p.getNetQuantity()
        );
    }

    @GetMapping("/portfolio/{portfolio}")
    public List<PortfolioPosition> byPortfolio(
            @PathVariable String portfolio) {
        return repo.findByPortfolio(portfolio);
    }
}
