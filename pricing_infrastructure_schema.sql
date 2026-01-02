-- ===============================================
-- PRICING ENGINE CONFIGURATION (Endur Style)
-- ===============================================

-- Pricing Engine Configuration (DB-Driven)
CREATE TABLE pricing_engine_config (
    config_id          BIGSERIAL PRIMARY KEY,
    instrument_type    VARCHAR(50) NOT NULL,
    pricing_model      VARCHAR(50) NOT NULL,  -- DCF, BLACK76, MONTE_CARLO
    engine_class       VARCHAR(200) NOT NULL, -- Java class name
    
    -- Model Parameters (JSON)
    default_params     JSONB,
    
    effective_from     TIMESTAMP,
    effective_to       TIMESTAMP,
    active             BOOLEAN DEFAULT TRUE,
    
    created_by         VARCHAR(50),
    created_at         TIMESTAMP DEFAULT now(),
    last_modified_by   VARCHAR(50),
    last_modified_at   TIMESTAMP,
    
    UNIQUE(instrument_type, pricing_model, effective_from)
);

CREATE INDEX idx_pricing_engine_active ON pricing_engine_config(instrument_type, active)
    WHERE active = TRUE;

-- Pricing Model Parameters (controlled dictionary)
CREATE TABLE pricing_model_parameter (
    param_id           BIGSERIAL PRIMARY KEY,
    config_id          BIGINT REFERENCES pricing_engine_config(config_id),
    param_name         VARCHAR(50) NOT NULL,   -- VOLATILITY, SPOT_PRICE, DISCOUNT_CURVE
    param_type         VARCHAR(20) NOT NULL,   -- CURVE, SURFACE, SCALAR
    data_source        VARCHAR(100),            -- Where to fetch from
    required           BOOLEAN DEFAULT TRUE,
    
    UNIQUE(config_id, param_name)
);

-- ===============================================
-- MARKET DATA INFRASTRUCTURE
-- ===============================================

-- Forward Curves (Commodities, Interest Rates, FX)
CREATE TABLE market_curve (
    curve_id           BIGSERIAL PRIMARY KEY,
    curve_name         VARCHAR(100) NOT NULL,
    commodity          VARCHAR(50),
    curve_type         VARCHAR(30) NOT NULL,  -- FORWARD, DISCOUNT, FX
    currency           VARCHAR(3),
    pricing_date       DATE NOT NULL,
    source             VARCHAR(50),            -- BLOOMBERG, REUTERS, MANUAL
    
    created_at         TIMESTAMP DEFAULT now(),
    created_by         VARCHAR(50),
    
    UNIQUE(curve_name, pricing_date)
);

CREATE INDEX idx_curve_date ON market_curve(curve_name, pricing_date);

-- Curve Points (Tenor structure)
CREATE TABLE market_curve_point (
    point_id           BIGSERIAL PRIMARY KEY,
    curve_id           BIGINT REFERENCES market_curve(curve_id) ON DELETE CASCADE,
    tenor_date         DATE NOT NULL,
    tenor_label        VARCHAR(20),           -- 1M, 3M, 6M, 1Y, 2Y
    price_value        DECIMAL(20,6) NOT NULL,
    
    UNIQUE(curve_id, tenor_date)
);

CREATE INDEX idx_curve_point_tenor ON market_curve_point(curve_id, tenor_date);

-- Volatility Surfaces (Options pricing)
CREATE TABLE volatility_surface (
    surface_id         BIGSERIAL PRIMARY KEY,
    surface_name       VARCHAR(100) NOT NULL,
    underlying         VARCHAR(50) NOT NULL,
    surface_type       VARCHAR(30) NOT NULL,  -- IMPLIED, HISTORICAL
    pricing_date       DATE NOT NULL,
    currency           VARCHAR(3),
    
    created_at         TIMESTAMP DEFAULT now(),
    
    UNIQUE(surface_name, pricing_date)
);

CREATE INDEX idx_vol_surface_date ON volatility_surface(surface_name, pricing_date);

-- Volatility Points (Strike x Expiry grid)
CREATE TABLE volatility_point (
    point_id           BIGSERIAL PRIMARY KEY,
    surface_id         BIGINT REFERENCES volatility_surface(surface_id) ON DELETE CASCADE,
    strike             DECIMAL(20,6),
    expiry_date        DATE,
    implied_vol        DECIMAL(10,6) NOT NULL,  -- In decimal (e.g., 0.25 = 25%)
    
    UNIQUE(surface_id, strike, expiry_date)
);

CREATE INDEX idx_vol_point_strike_expiry ON volatility_point(surface_id, strike, expiry_date);

-- ===============================================
-- VALUATION RESULTS (Store detailed pricing output)
-- ===============================================

-- Valuation Run (Batch processing)
CREATE TABLE valuation_run (
    run_id             BIGSERIAL PRIMARY KEY,
    run_name           VARCHAR(100),
    valuation_date     DATE NOT NULL,
    portfolio_filter   VARCHAR(100),
    
    total_trades       INT,
    successful_count   INT,
    failed_count       INT,
    
    status             VARCHAR(20) NOT NULL,  -- RUNNING, COMPLETED, FAILED
    started_at         TIMESTAMP DEFAULT now(),
    completed_at       TIMESTAMP,
    started_by         VARCHAR(50)
);

-- Detailed Valuation Result (per trade)
CREATE TABLE valuation_result (
    result_id          BIGSERIAL PRIMARY KEY,
    trade_id           BIGINT REFERENCES trades(id),
    valuation_run_id   BIGINT REFERENCES valuation_run(run_id),
    pricing_date       DATE NOT NULL,
    
    -- MTM Components
    mtm_total          DECIMAL(20,6),
    mtm_spot           DECIMAL(20,6),
    mtm_forward        DECIMAL(20,6),
    mtm_time_value     DECIMAL(20,6),
    
    -- Greeks (for options and non-linear products)
    delta              DECIMAL(20,6),
    gamma              DECIMAL(20,6),
    vega               DECIMAL(20,6),
    theta              DECIMAL(20,6),
    rho                DECIMAL(20,6),
    
    -- Risk Metrics
    var_1day           DECIMAL(20,6),
    stress_test_result DECIMAL(20,6),
    
    -- Valuation Context (store what was used)
    market_context     JSONB,
    pricing_context    JSONB,
    risk_context       JSONB,
    
    -- Metadata
    pricing_model      VARCHAR(50),
    calc_timestamp     TIMESTAMP DEFAULT now(),
    calc_duration_ms   INT,
    warnings           TEXT[],
    
    UNIQUE(trade_id, pricing_date, valuation_run_id)
);

CREATE INDEX idx_valuation_trade ON valuation_result(trade_id, pricing_date);
CREATE INDEX idx_valuation_run ON valuation_result(valuation_run_id);

-- ===============================================
-- P&L ATTRIBUTION
-- ===============================================

CREATE TABLE pnl_explain (
    explain_id         BIGSERIAL PRIMARY KEY,
    trade_id           BIGINT REFERENCES trades(id),
    pnl_date           DATE NOT NULL,
    
    -- Total P&L
    total_pnl          DECIMAL(20,6) NOT NULL,
    
    -- Attribution by source
    pnl_spot_move      DECIMAL(20,6) DEFAULT 0,
    pnl_curve_move     DECIMAL(20,6) DEFAULT 0,
    pnl_vol_move       DECIMAL(20,6) DEFAULT 0,
    pnl_time_decay     DECIMAL(20,6) DEFAULT 0,
    pnl_fx_impact      DECIMAL(20,6) DEFAULT 0,
    pnl_carry          DECIMAL(20,6) DEFAULT 0,
    pnl_new_trades     DECIMAL(20,6) DEFAULT 0,
    
    unexplained        DECIMAL(20,6) DEFAULT 0,
    
    -- Reference to valuations
    valuation_t0       BIGINT REFERENCES valuation_result(result_id),
    valuation_t1       BIGINT REFERENCES valuation_result(result_id),
    
    created_at         TIMESTAMP DEFAULT now(),
    
    UNIQUE(trade_id, pnl_date)
);

CREATE INDEX idx_pnl_date ON pnl_explain(pnl_date);

-- ===============================================
-- SEED DATA - Sample Configuration
-- ===============================================

-- Configure Power Forward pricing engine
INSERT INTO pricing_engine_config (
    instrument_type, 
    pricing_model, 
    engine_class, 
    default_params, 
    active
) VALUES (
    'POWER_FORWARD',
    'DCF',
    'com.trading.ctrm.pricing.PowerForwardPricingEngine',
    '{"discountCurve": "USD_CURVES", "interpolationMethod": "LINEAR"}',
    true
);

-- Add pricing parameters
INSERT INTO pricing_model_parameter (config_id, param_name, param_type, data_source, required)
SELECT 
    config_id,
    unnest(ARRAY['FORWARD_CURVE', 'DISCOUNT_CURVE', 'FX_RATE']) as param_name,
    unnest(ARRAY['CURVE', 'CURVE', 'SCALAR']) as param_type,
    unnest(ARRAY['market_curve', 'market_curve', 'market_data_snapshot']) as data_source,
    true
FROM pricing_engine_config 
WHERE instrument_type = 'POWER_FORWARD' AND pricing_model = 'DCF';

-- Sample market curve for testing
INSERT INTO market_curve (curve_name, commodity, curve_type, currency, pricing_date, source)
VALUES ('POWER_FORWARD_CURVE', 'ELECTRICITY', 'FORWARD', 'USD', CURRENT_DATE, 'MANUAL');

-- Sample curve points
INSERT INTO market_curve_point (curve_id, tenor_date, tenor_label, price_value)
SELECT 
    c.curve_id,
    CURRENT_DATE + (interval '1 month' * n),
    n || 'M',
    50.0 + (n * 0.5) + (random() * 5)
FROM market_curve c
CROSS JOIN generate_series(1, 24) as n
WHERE c.curve_name = 'POWER_FORWARD_CURVE'
AND c.pricing_date = CURRENT_DATE;

COMMENT ON TABLE pricing_engine_config IS 'Database-driven pricing engine configuration - Endur style';
COMMENT ON TABLE market_curve IS 'Forward curves for commodities, interest rates, and FX';
COMMENT ON TABLE valuation_result IS 'Detailed pricing results with Greeks and risk metrics';
COMMENT ON TABLE pnl_explain IS 'P&L attribution by risk factor';
