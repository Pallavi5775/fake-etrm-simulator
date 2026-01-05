-- Remove All Existing Trades
-- This script removes all trades from the CTRM system

SET search_path TO ctrm;

-- Show trades before deletion
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

-- Remove all trades
DELETE FROM trades;

-- Verify all trades are removed
SELECT COUNT(*) as remaining_trades FROM trades;

COMMIT;