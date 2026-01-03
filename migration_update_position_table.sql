-- Migration: Update ctrm.position table to match Java Position entity
-- Add missing columns for position aggregation and reporting



-- Optional: Remove NOT NULL from net_quantity if needed
-- ALTER TABLE ctrm."position" ALTER COLUMN net_quantity DROP NOT NULL;

-- Optional: Add indexes for reporting performance
CREATE INDEX IF NOT EXISTS idx_position_date ON ctrm."position" (position_date);
CREATE INDEX IF NOT EXISTS idx_position_portfolio ON ctrm."position" (portfolio);
CREATE INDEX IF NOT EXISTS idx_position_commodity ON ctrm."position" (commodity);