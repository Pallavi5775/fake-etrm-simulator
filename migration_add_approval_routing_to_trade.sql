-- Add columns for multi-level approval tracking to trade table
-- Run this script on your PostgreSQL database

ALTER TABLE ctrm.trade 
ADD COLUMN current_approval_level INTEGER,
ADD COLUMN matched_rule_id BIGINT;

-- Add comments for documentation
COMMENT ON COLUMN ctrm.trade.current_approval_level IS 'Tracks which approval level (1, 2, 3...) is currently required';
COMMENT ON COLUMN ctrm.trade.matched_rule_id IS 'Foreign key to approval_rule.rule_id that was matched when trade was booked';
