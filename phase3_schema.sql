-- ============================================================================
-- PHASE 3 SCHEMA: Risk Management & Trade Versioning
-- ============================================================================
-- Trade versioning, position aggregation, risk limits, VaR
-- Designed for Endur-style enterprise risk management
-- ============================================================================

-- ============================================================================
-- 1. TRADE VERSION
-- ============================================================================
-- Complete audit trail of trade amendments
CREATE TABLE IF NOT EXISTS trade_version (
    version_id          BIGSERIAL PRIMARY KEY,
    trade_id            BIGINT NOT NULL,
    version_number      INT NOT NULL,
    version_type        VARCHAR(30) NOT NULL,  -- ORIGINAL, AMENDMENT, CANCELLATION
    
    -- Complete trade snapshot (JSON)
    trade_snapshot      TEXT NOT NULL,
    
    -- Change tracking
    change_description  TEXT,
    change_diff         TEXT,  -- JSON diff of changes
    
    -- Metadata
    amended_by          VARCHAR(50),
    amended_at          TIMESTAMP NOT NULL DEFAULT now(),
    amendment_reason    VARCHAR(500),
    approval_id         BIGINT,
    
    CONSTRAINT uq_trade_version UNIQUE (trade_id, version_number)
);

CREATE INDEX idx_trade_version_trade ON trade_version(trade_id);
CREATE INDEX idx_trade_version_type ON trade_version(version_type);
CREATE INDEX idx_trade_version_user ON trade_version(amended_by);
CREATE INDEX idx_trade_version_date ON trade_version(amended_at);

-- ============================================================================
-- 2. POSITION
-- ============================================================================
-- Aggregated positions by portfolio/commodity/delivery period
CREATE TABLE IF NOT EXISTS position (
    position_id         BIGSERIAL PRIMARY KEY,
    position_date       DATE NOT NULL,
    portfolio           VARCHAR(50) NOT NULL,
    commodity           VARCHAR(50) NOT NULL,
    delivery_start      DATE,
    delivery_end        DATE,
    
    -- Aggregated quantities
    long_quantity       DECIMAL(20,6) DEFAULT 0,
    short_quantity      DECIMAL(20,6) DEFAULT 0,
    net_quantity        DECIMAL(20,6) DEFAULT 0,
    
    -- Aggregated values
    long_mtm            DECIMAL(20,6) DEFAULT 0,
    short_mtm           DECIMAL(20,6) DEFAULT 0,
    net_mtm             DECIMAL(20,6) DEFAULT 0,
    
    -- Risk metrics
    delta               DECIMAL(20,6) DEFAULT 0,
    gamma               DECIMAL(20,6) DEFAULT 0,
    vega                DECIMAL(20,6) DEFAULT 0,
    
    -- Metadata
    trade_count         INT DEFAULT 0,
    last_updated        TIMESTAMP DEFAULT now()
);

CREATE INDEX idx_position_date ON position(position_date);
CREATE INDEX idx_position_portfolio ON position(portfolio);
CREATE INDEX idx_position_commodity ON position(commodity);
CREATE INDEX idx_position_portfolio_date ON position(portfolio, position_date);
CREATE INDEX idx_position_commodity_date ON position(commodity, position_date);

-- ============================================================================
-- 3. RISK LIMIT
-- ============================================================================
-- Risk limit definitions
CREATE TABLE IF NOT EXISTS risk_limit (
    limit_id            BIGSERIAL PRIMARY KEY,
    limit_name          VARCHAR(100) NOT NULL,
    limit_type          VARCHAR(30) NOT NULL,  -- POSITION, VAR, DELTA, CONCENTRATION
    limit_scope         VARCHAR(30) NOT NULL,  -- PORTFOLIO, COMMODITY, COUNTERPARTY
    scope_value         VARCHAR(100),          -- Specific entity
    
    -- Limit values
    limit_value         DECIMAL(20,6),
    warning_threshold   DECIMAL(20,6),
    limit_unit          VARCHAR(20),           -- MWh, USD, PERCENT
    
    -- Status
    active              BOOLEAN DEFAULT TRUE,
    breach_action       VARCHAR(30),           -- ALERT, BLOCK, ESCALATE
    
    -- Metadata
    created_by          VARCHAR(50),
    created_at          TIMESTAMP DEFAULT now(),
    last_modified_by    VARCHAR(50),
    last_modified_at    TIMESTAMP DEFAULT now()
);

CREATE INDEX idx_risk_limit_type ON risk_limit(limit_type);
CREATE INDEX idx_risk_limit_scope ON risk_limit(limit_scope, scope_value);
CREATE INDEX idx_risk_limit_active ON risk_limit(active);

-- ============================================================================
-- 4. RISK LIMIT BREACH
-- ============================================================================
-- Track limit violations
CREATE TABLE IF NOT EXISTS risk_limit_breach (
    breach_id           BIGSERIAL PRIMARY KEY,
    limit_id            BIGINT NOT NULL REFERENCES risk_limit(limit_id),
    breach_date         TIMESTAMP NOT NULL DEFAULT now(),
    
    -- Breach details
    current_value       DECIMAL(20,6),
    limit_value         DECIMAL(20,6),
    breach_amount       DECIMAL(20,6),
    breach_percent      DECIMAL(10,6),
    
    -- Classification
    severity            VARCHAR(20),           -- WARNING, BREACH, CRITICAL
    breach_status       VARCHAR(20) DEFAULT 'ACTIVE',  -- ACTIVE, RESOLVED, ACKNOWLEDGED
    
    -- Resolution
    resolution_notes    TEXT,
    resolved_by         VARCHAR(50),
    resolved_at         TIMESTAMP
);

CREATE INDEX idx_breach_limit ON risk_limit_breach(limit_id);
CREATE INDEX idx_breach_status ON risk_limit_breach(breach_status);
CREATE INDEX idx_breach_severity ON risk_limit_breach(severity);
CREATE INDEX idx_breach_date ON risk_limit_breach(breach_date);

-- ============================================================================
-- 5. SAMPLE DATA
-- ============================================================================

-- Sample risk limits
INSERT INTO risk_limit (limit_name, limit_type, limit_scope, scope_value, limit_value, warning_threshold, limit_unit, breach_action, created_by)
VALUES 
    ('Power Trading - Net Position', 'POSITION', 'PORTFOLIO', 'POWER_TRADING', 10000, 8000, 'MWh', 'ALERT', 'SYSTEM'),
    ('Gas Trading - Net Position', 'POSITION', 'PORTFOLIO', 'GAS_TRADING', 50000, 40000, 'MMBtu', 'ALERT', 'SYSTEM'),
    ('Power Trading - VaR Limit', 'VAR', 'PORTFOLIO', 'POWER_TRADING', 1000000, 800000, 'USD', 'BLOCK', 'SYSTEM'),
    ('Power - Delta Limit', 'DELTA', 'COMMODITY', 'POWER', 5000, 4000, 'MWh', 'ALERT', 'SYSTEM'),
    ('Counterparty A - Concentration', 'CONCENTRATION', 'COUNTERPARTY', 'COUNTERPARTY_A', 5000000, 4000000, 'USD', 'ALERT', 'SYSTEM')
ON CONFLICT DO NOTHING;

-- ============================================================================
-- 6. VIEWS FOR REPORTING
-- ============================================================================

-- Position Summary View
CREATE OR REPLACE VIEW v_position_summary AS
SELECT 
    p.position_date,
    p.portfolio,
    COUNT(DISTINCT p.commodity) as commodity_count,
    SUM(p.long_quantity) as total_long,
    SUM(p.short_quantity) as total_short,
    SUM(p.net_quantity) as total_net,
    SUM(p.net_mtm) as total_mtm,
    SUM(p.delta) as total_delta,
    SUM(p.trade_count) as total_trades
FROM position p
GROUP BY p.position_date, p.portfolio
ORDER BY p.position_date DESC, p.portfolio;

-- Trade Amendment History View
CREATE OR REPLACE VIEW v_trade_amendment_history AS
SELECT 
    tv.trade_id,
    COUNT(*) as total_versions,
    COUNT(*) FILTER (WHERE tv.version_type = 'AMENDMENT') as amendment_count,
    MAX(tv.version_number) as current_version,
    MAX(tv.amended_at) as last_amended,
    STRING_AGG(DISTINCT tv.amended_by, ', ') as amended_by_users
FROM trade_version tv
GROUP BY tv.trade_id
ORDER BY MAX(tv.amended_at) DESC;

-- Risk Limit Status View
CREATE OR REPLACE VIEW v_risk_limit_status AS
SELECT 
    rl.limit_id,
    rl.limit_name,
    rl.limit_type,
    rl.limit_scope,
    rl.scope_value,
    rl.limit_value,
    rl.warning_threshold,
    rl.active,
    COUNT(rlb.breach_id) FILTER (WHERE rlb.breach_status = 'ACTIVE') as active_breaches,
    MAX(rlb.breach_date) as last_breach_date
FROM risk_limit rl
LEFT JOIN risk_limit_breach rlb ON rl.limit_id = rlb.limit_id
GROUP BY rl.limit_id, rl.limit_name, rl.limit_type, rl.limit_scope, rl.scope_value, rl.limit_value, rl.warning_threshold, rl.active
ORDER BY rl.limit_type, rl.limit_name;

-- Active Breaches Summary
CREATE OR REPLACE VIEW v_active_breaches AS
SELECT 
    rlb.breach_id,
    rlb.breach_date,
    rlb.severity,
    rl.limit_name,
    rl.limit_type,
    rl.limit_scope,
    rl.scope_value,
    rlb.current_value,
    rlb.limit_value,
    rlb.breach_amount,
    rlb.breach_percent,
    rl.breach_action
FROM risk_limit_breach rlb
JOIN risk_limit rl ON rlb.limit_id = rl.limit_id
WHERE rlb.breach_status = 'ACTIVE'
ORDER BY rlb.severity DESC, rlb.breach_date DESC;

-- Portfolio Risk Summary
CREATE OR REPLACE VIEW v_portfolio_risk_summary AS
SELECT 
    p.portfolio,
    p.position_date,
    COUNT(DISTINCT p.commodity) as commodities,
    SUM(p.net_quantity) as net_position,
    SUM(p.net_mtm) as portfolio_mtm,
    SUM(ABS(p.delta)) as total_delta,
    SUM(p.trade_count) as trade_count,
    COUNT(rlb.breach_id) as limit_breaches
FROM position p
LEFT JOIN risk_limit rl ON rl.limit_scope = 'PORTFOLIO' AND rl.scope_value = p.portfolio
LEFT JOIN risk_limit_breach rlb ON rlb.limit_id = rl.limit_id AND rlb.breach_status = 'ACTIVE'
GROUP BY p.portfolio, p.position_date
ORDER BY p.position_date DESC, p.portfolio;

-- ============================================================================
-- 7. FUNCTIONS
-- ============================================================================

-- Function to get trade at specific version
CREATE OR REPLACE FUNCTION get_trade_version(p_trade_id BIGINT, p_version_number INT)
RETURNS TEXT AS $$
DECLARE
    v_snapshot TEXT;
BEGIN
    SELECT trade_snapshot INTO v_snapshot
    FROM trade_version
    WHERE trade_id = p_trade_id AND version_number = p_version_number;
    
    RETURN v_snapshot;
END;
$$ LANGUAGE plpgsql;

-- Function to count amendments for a trade
CREATE OR REPLACE FUNCTION count_trade_amendments(p_trade_id BIGINT)
RETURNS INT AS $$
DECLARE
    v_count INT;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM trade_version
    WHERE trade_id = p_trade_id AND version_type = 'AMENDMENT';
    
    RETURN v_count;
END;
$$ LANGUAGE plpgsql;

-- ============================================================================
-- COMMENTS
-- ============================================================================

COMMENT ON TABLE trade_version IS 'Complete audit trail of trade amendments with snapshots';
COMMENT ON TABLE position IS 'Aggregated trading positions by portfolio and commodity';
COMMENT ON TABLE risk_limit IS 'Risk limit definitions for position, VaR, and concentration limits';
COMMENT ON TABLE risk_limit_breach IS 'Track violations of risk limits';

COMMENT ON VIEW v_position_summary IS 'Aggregated position summary by portfolio and date';
COMMENT ON VIEW v_trade_amendment_history IS 'Amendment statistics per trade';
COMMENT ON VIEW v_risk_limit_status IS 'Risk limit status with breach counts';
COMMENT ON VIEW v_active_breaches IS 'Current active limit breaches';
COMMENT ON VIEW v_portfolio_risk_summary IS 'Portfolio-level risk metrics and breach counts';

-- ============================================================================
-- END OF PHASE 3 SCHEMA
-- ============================================================================
