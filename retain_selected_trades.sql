-- Remove Unwanted Trades, Keep Only OPT_FUT_002 and OPT_FWD_002
-- This script removes all trades except the two specified ones

SET search_path TO ctrm;

-- Show all trades before deletion
SELECT
    t.trade_id,
    i.instrument_code,
    t.quantity,
    t.price,
    t.buy_sell,
    t.counterparty,
    t.portfolio,
    t.status
FROM trades t
JOIN instruments i ON t.instrument_id = i.id
ORDER BY t.trade_id;

-- Remove unwanted trades (keep only OPT_FUT_002 and OPT_FWD_002)
DELETE FROM trades
WHERE trade_id NOT IN ('OPT_FUT_002', 'OPT_FWD_002');

-- Verify only the desired trades remain
SELECT
    t.trade_id,
    i.instrument_code,
    t.quantity,
    t.price,
    t.buy_sell,
    t.counterparty,
    t.portfolio,
    t.status
FROM trades t
JOIN instruments i ON t.instrument_id = i.id
ORDER BY t.trade_id;

COMMIT;