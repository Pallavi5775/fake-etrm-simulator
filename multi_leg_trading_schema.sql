-- Multi-Leg Trading Support Schema

-- Table for individual trade legs
CREATE TABLE IF NOT EXISTS ctrm.trade_legs (
    id BIGSERIAL PRIMARY KEY,
    trade_id VARCHAR(50) NOT NULL REFERENCES ctrm.trades(trade_id),
    leg_number INT NOT NULL,
    instrument_id BIGINT NOT NULL REFERENCES ctrm.instruments(id),
    buy_sell VARCHAR(10) NOT NULL,
    quantity NUMERIC(19,4) NOT NULL,
    price NUMERIC(19,4) NOT NULL,
    ratio NUMERIC(10,4) DEFAULT 1.0,
    delivery_date DATE,
    mtm NUMERIC(19,4),
    created_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT unique_trade_leg UNIQUE (trade_id, leg_number)
);

-- Add strategy type to trades table
ALTER TABLE ctrm.trades 
ADD COLUMN IF NOT EXISTS strategy_type VARCHAR(50),
ADD COLUMN IF NOT EXISTS is_multi_leg BOOLEAN DEFAULT false;

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_trade_legs_trade_id ON ctrm.trade_legs(trade_id);
CREATE INDEX IF NOT EXISTS idx_trade_legs_instrument ON ctrm.trade_legs(instrument_id);
CREATE INDEX IF NOT EXISTS idx_trades_strategy ON ctrm.trades(strategy_type);
CREATE INDEX IF NOT EXISTS idx_trades_multi_leg ON ctrm.trades(is_multi_leg);

-- Grant permissions
GRANT SELECT, INSERT, UPDATE, DELETE ON ctrm.trade_legs TO ctrm_user;
GRANT USAGE, SELECT ON SEQUENCE ctrm.trade_legs_id_seq TO ctrm_user;

-- Comments for documentation
COMMENT ON TABLE ctrm.trade_legs IS 'Individual legs of multi-leg trade structures (spreads, butterflies, etc)';
COMMENT ON COLUMN ctrm.trade_legs.leg_number IS 'Sequence number of leg within the trade (1, 2, 3...)';
COMMENT ON COLUMN ctrm.trade_legs.ratio IS 'Quantity ratio for complex spreads (e.g., 1:2:1 butterfly)';
COMMENT ON COLUMN ctrm.trades.strategy_type IS 'Type of multi-leg strategy: CALENDAR_SPREAD, CRACK_SPREAD, BUTTERFLY, STRADDLE, etc.';
