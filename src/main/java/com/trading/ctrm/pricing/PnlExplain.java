package com.trading.ctrm.pricing;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * P&L Explanation - attribution by risk factor (Endur-style)
 */
@Entity
@Table(name = "pnl_explain")
public class PnlExplain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "explain_id")
    private Long explainId;

    @Column(name = "trade_id", nullable = false)
    private Long tradeId;

    @Column(name = "pnl_date", nullable = false)
    private LocalDate pnlDate;

    // Total P&L
    @Column(name = "total_pnl", nullable = false, precision = 20, scale = 6)
    private BigDecimal totalPnl;

    // Attribution by source
    @Column(name = "pnl_spot_move", precision = 20, scale = 6)
    private BigDecimal pnlSpotMove = BigDecimal.ZERO;

    @Column(name = "pnl_curve_move", precision = 20, scale = 6)
    private BigDecimal pnlCurveMove = BigDecimal.ZERO;

    @Column(name = "pnl_vol_move", precision = 20, scale = 6)
    private BigDecimal pnlVolMove = BigDecimal.ZERO;

    @Column(name = "pnl_time_decay", precision = 20, scale = 6)
    private BigDecimal pnlTimeDecay = BigDecimal.ZERO;

    @Column(name = "pnl_fx_impact", precision = 20, scale = 6)
    private BigDecimal pnlFxImpact = BigDecimal.ZERO;

    @Column(name = "pnl_carry", precision = 20, scale = 6)
    private BigDecimal pnlCarry = BigDecimal.ZERO;

    @Column(name = "pnl_new_trades", precision = 20, scale = 6)
    private BigDecimal pnlNewTrades = BigDecimal.ZERO;

    @Column(precision = 20, scale = 6)
    private BigDecimal unexplained = BigDecimal.ZERO;

    // Reference to valuations
    @Column(name = "valuation_t0")
    private Long valuationT0;

    @Column(name = "valuation_t1")
    private Long valuationT1;

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;

    // Constructors
    public PnlExplain() {
        this.createdAt = java.time.LocalDateTime.now();
    }

    // Getters and Setters
    public Long getExplainId() { return explainId; }
    public void setExplainId(Long explainId) { this.explainId = explainId; }

    public Long getTradeId() { return tradeId; }
    public void setTradeId(Long tradeId) { this.tradeId = tradeId; }

    public LocalDate getPnlDate() { return pnlDate; }
    public void setPnlDate(LocalDate pnlDate) { this.pnlDate = pnlDate; }

    public BigDecimal getTotalPnl() { return totalPnl; }
    public void setTotalPnl(BigDecimal totalPnl) { this.totalPnl = totalPnl; }

    public BigDecimal getPnlSpotMove() { return pnlSpotMove; }
    public void setPnlSpotMove(BigDecimal pnlSpotMove) { this.pnlSpotMove = pnlSpotMove; }

    public BigDecimal getPnlCurveMove() { return pnlCurveMove; }
    public void setPnlCurveMove(BigDecimal pnlCurveMove) { this.pnlCurveMove = pnlCurveMove; }

    public BigDecimal getPnlVolMove() { return pnlVolMove; }
    public void setPnlVolMove(BigDecimal pnlVolMove) { this.pnlVolMove = pnlVolMove; }

    public BigDecimal getPnlTimeDecay() { return pnlTimeDecay; }
    public void setPnlTimeDecay(BigDecimal pnlTimeDecay) { this.pnlTimeDecay = pnlTimeDecay; }

    public BigDecimal getPnlFxImpact() { return pnlFxImpact; }
    public void setPnlFxImpact(BigDecimal pnlFxImpact) { this.pnlFxImpact = pnlFxImpact; }

    public BigDecimal getPnlCarry() { return pnlCarry; }
    public void setPnlCarry(BigDecimal pnlCarry) { this.pnlCarry = pnlCarry; }

    public BigDecimal getPnlNewTrades() { return pnlNewTrades; }
    public void setPnlNewTrades(BigDecimal pnlNewTrades) { this.pnlNewTrades = pnlNewTrades; }

    public BigDecimal getUnexplained() { return unexplained; }
    public void setUnexplained(BigDecimal unexplained) { this.unexplained = unexplained; }

    public Long getValuationT0() { return valuationT0; }
    public void setValuationT0(Long valuationT0) { this.valuationT0 = valuationT0; }

    public Long getValuationT1() { return valuationT1; }
    public void setValuationT1(Long valuationT1) { this.valuationT1 = valuationT1; }

    public java.time.LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
}
