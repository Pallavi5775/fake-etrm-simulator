-- ============================================================================
-- PHASE 2 SCHEMA: Advanced Pricing Infrastructure
-- ============================================================================
-- Volatility surfaces, scenario framework, additional indexes
-- Designed for Endur-style stress testing and P&L attribution
-- ============================================================================

-- ============================================================================
-- 1. VALUATION SCENARIO
-- ============================================================================
-- Scenarios for what-if analysis and stress testing
CREATE TABLE IF NOT EXISTS valuation_scenario (
    scenario_id       BIGSERIAL PRIMARY KEY,
    scenario_name     VARCHAR(100) NOT NULL,
    description       TEXT,
    scenario_type     VARCHAR(30) NOT NULL,  -- SPOT_SHOCK, CURVE_SHIFT, VOL_SHOCK, HISTORICAL
    base_date         DATE NOT NULL,
    
    -- Scenario parameters (JSON)
    parameters        TEXT,
    
    created_by        VARCHAR(50),
    created_at        TIMESTAMP DEFAULT now()
);

CREATE INDEX idx_scenario_type ON valuation_scenario(scenario_type);
CREATE INDEX idx_scenario_base_date ON valuation_scenario(base_date);

-- ============================================================================
-- 2. SCENARIO RESULT
-- ============================================================================
-- Results of scenario analysis by trade
CREATE TABLE IF NOT EXISTS scenario_result (
    result_id         BIGSERIAL PRIMARY KEY,
    scenario_id       BIGINT NOT NULL REFERENCES valuation_scenario(scenario_id),
    trade_id          BIGINT NOT NULL,
    
    -- Valuations
    base_mtm          DECIMAL(20,6),
    scenario_mtm      DECIMAL(20,6),
    
    -- Impact
    pnl_impact        DECIMAL(20,6),
    pnl_impact_pct    DECIMAL(10,6),
    
    created_at        TIMESTAMP DEFAULT now()
);

CREATE INDEX idx_scenario_result_scenario ON scenario_result(scenario_id);
CREATE INDEX idx_scenario_result_trade ON scenario_result(trade_id);
CREATE INDEX idx_scenario_result_impact ON scenario_result(pnl_impact);

-- ============================================================================
-- 3. ADDITIONAL INDEXES FOR PERFORMANCE
-- ============================================================================

-- Valuation Result indexes for P&L queries
CREATE INDEX IF NOT EXISTS idx_valuation_result_trade_date 
    ON valuation_result(trade_id, valuation_date);

CREATE INDEX IF NOT EXISTS idx_valuation_result_run 
    ON valuation_result(run_id);

CREATE INDEX IF NOT EXISTS idx_valuation_result_date 
    ON valuation_result(valuation_date);

-- P&L Explain indexes
CREATE INDEX IF NOT EXISTS idx_pnl_explain_date 
    ON pnl_explain(pnl_date);

CREATE INDEX IF NOT EXISTS idx_pnl_explain_trade_date 
    ON pnl_explain(trade_id, pnl_date);

CREATE INDEX IF NOT EXISTS idx_pnl_explain_unexplained 
    ON pnl_explain(unexplained) WHERE ABS(unexplained) > 100;

-- Valuation Run indexes
CREATE INDEX IF NOT EXISTS idx_valuation_run_date 
    ON valuation_run(valuation_date);

CREATE INDEX IF NOT EXISTS idx_valuation_run_status 
    ON valuation_run(status);

-- Market Curve indexes
CREATE INDEX IF NOT EXISTS idx_market_curve_name_date 
    ON market_curve(curve_name, pricing_date);

CREATE INDEX IF NOT EXISTS idx_market_curve_underlying 
    ON market_curve(underlying);

-- Volatility Surface indexes
CREATE INDEX IF NOT EXISTS idx_vol_surface_underlying_date 
    ON volatility_surface(underlying, pricing_date);

CREATE INDEX IF NOT EXISTS idx_vol_surface_type 
    ON volatility_surface(surface_type);

-- ============================================================================
-- 4. SAMPLE DATA FOR TESTING
-- ============================================================================

-- Sample scenarios
INSERT INTO valuation_scenario (scenario_name, scenario_type, base_date, description, parameters, created_by)
VALUES 
    ('10% Spot Shock Up', 'SPOT_SHOCK', CURRENT_DATE, 'Spot price +10%', '{"spotShock": 10}', 'SYSTEM'),
    ('10% Spot Shock Down', 'SPOT_SHOCK', CURRENT_DATE, 'Spot price -10%', '{"spotShock": -10}', 'SYSTEM'),
    ('Parallel Curve Shift +5', 'CURVE_SHIFT', CURRENT_DATE, 'Forward curve +$5', '{"curveShift": 5}', 'SYSTEM'),
    ('Vol Shock +20%', 'VOL_SHOCK', CURRENT_DATE, 'Implied vol +20%', '{"volShock": 20}', 'SYSTEM'),
    ('2008 Financial Crisis', 'HISTORICAL', '2008-09-15', 'Lehman Brothers collapse', '{"historicalDate": "2008-09-15"}', 'SYSTEM')
ON CONFLICT DO NOTHING;

-- Sample volatility surface (ATM vols for Power)
INSERT INTO volatility_surface (surface_name, underlying, surface_type, pricing_date, currency)
VALUES ('POWER_ATM_VOL', 'POWER', 'IMPLIED', CURRENT_DATE, 'USD')
ON CONFLICT DO NOTHING;

-- Get the surface_id for inserting points
DO $$
DECLARE
    v_surface_id BIGINT;
BEGIN
    SELECT surface_id INTO v_surface_id 
    FROM volatility_surface 
    WHERE surface_name = 'POWER_ATM_VOL' 
    AND pricing_date = CURRENT_DATE
    LIMIT 1;
    
    IF v_surface_id IS NOT NULL THEN
        -- Insert volatility points (strike = ATM, various expiries)
        INSERT INTO volatility_point (surface_id, strike, expiry_date, implied_vol)
        VALUES 
            (v_surface_id, NULL, CURRENT_DATE + INTERVAL '1 month', 0.25),
            (v_surface_id, NULL, CURRENT_DATE + INTERVAL '3 months', 0.28),
            (v_surface_id, NULL, CURRENT_DATE + INTERVAL '6 months', 0.30),
            (v_surface_id, NULL, CURRENT_DATE + INTERVAL '1 year', 0.32),
            (v_surface_id, NULL, CURRENT_DATE + INTERVAL '2 years', 0.35)
        ON CONFLICT DO NOTHING;
    END IF;
END $$;

-- ============================================================================
-- 5. VIEWS FOR REPORTING
-- ============================================================================

-- Portfolio P&L Summary
CREATE OR REPLACE VIEW v_portfolio_pnl_summary AS
SELECT 
    pe.pnl_date,
    COUNT(DISTINCT pe.trade_id) as trade_count,
    SUM(pe.total_pnl) as total_pnl,
    SUM(pe.pnl_spot_move) as total_spot_pnl,
    SUM(pe.pnl_curve_move) as total_curve_pnl,
    SUM(pe.pnl_vol_move) as total_vol_pnl,
    SUM(pe.pnl_time_decay) as total_theta,
    SUM(pe.unexplained) as total_unexplained,
    AVG(ABS(pe.unexplained)) as avg_unexplained
FROM pnl_explain pe
GROUP BY pe.pnl_date
ORDER BY pe.pnl_date DESC;

-- Scenario Impact Summary
CREATE OR REPLACE VIEW v_scenario_impact_summary AS
SELECT 
    vs.scenario_id,
    vs.scenario_name,
    vs.scenario_type,
    vs.base_date,
    COUNT(sr.result_id) as trade_count,
    SUM(sr.pnl_impact) as total_impact,
    AVG(sr.pnl_impact) as avg_impact,
    MAX(sr.pnl_impact) as max_impact,
    MIN(sr.pnl_impact) as min_impact,
    STDDEV(sr.pnl_impact) as stddev_impact
FROM valuation_scenario vs
LEFT JOIN scenario_result sr ON vs.scenario_id = sr.scenario_id
GROUP BY vs.scenario_id, vs.scenario_name, vs.scenario_type, vs.base_date
ORDER BY vs.created_at DESC;

-- Valuation Run Summary
CREATE OR REPLACE VIEW v_valuation_run_summary AS
SELECT 
    vr.run_id,
    vr.run_name,
    vr.valuation_date,
    vr.portfolio_filter,
    vr.status,
    vr.total_trades,
    vr.successful_count,
    vr.failed_count,
    ROUND(100.0 * vr.successful_count / NULLIF(vr.total_trades, 0), 2) as success_rate_pct,
    EXTRACT(EPOCH FROM (vr.completed_at - vr.started_at)) as duration_seconds,
    vr.started_by,
    vr.started_at,
    vr.completed_at
FROM valuation_run vr
ORDER BY vr.started_at DESC;

-- ============================================================================
-- COMMENTS
-- ============================================================================

COMMENT ON TABLE valuation_scenario IS 'Scenario definitions for stress testing and what-if analysis';
COMMENT ON TABLE scenario_result IS 'Results of scenario analysis by trade';
COMMENT ON VIEW v_portfolio_pnl_summary IS 'Daily P&L summary with attribution breakdown';
COMMENT ON VIEW v_scenario_impact_summary IS 'Scenario analysis summary statistics';
COMMENT ON VIEW v_valuation_run_summary IS 'Batch valuation run performance metrics';

-- ============================================================================
-- END OF PHASE 2 SCHEMA
-- ============================================================================
