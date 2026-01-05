-- Book Two Options Trades: One Futures and One Forward
-- This script creates sample options trades in the CTRM system

SET search_path TO ctrm;

-- Get instrument IDs for the options we want to trade
CREATE TEMP TABLE trade_instruments AS
SELECT id, instrument_code FROM instruments
WHERE instrument_code IN ('WTI_FUTURES_CALL_2026', 'POWER_BASELOAD_FORWARD_CALL_2026');

-- Trade 1: Futures Option - Buy WTI Call
INSERT INTO trades (
    trade_id, instrument_id, counterparty, portfolio, quantity, price, buy_sell,
    trade_date, created_by, status, created_at, updated_at
) VALUES (
    'OPT_FUT_002',
    (SELECT id FROM trade_instruments WHERE instrument_code = 'WTI_FUTURES_CALL_2026'),
    'Shell Energy',
    'ENERGY_PORTFOLIO',
    100.00,  -- 100 lots
    2.50,    -- $2.50 premium per lot
    'BUY',
    CURRENT_DATE,
    'trader_user',
    'PENDING_APPROVAL',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Trade 2: Forward Option - Buy Power Call
INSERT INTO trades (
    trade_id, instrument_id, counterparty, portfolio, quantity, price, buy_sell,
    trade_date, created_by, status, created_at, updated_at
) VALUES (
    'OPT_FWD_002',
    (SELECT id FROM trade_instruments WHERE instrument_code = 'POWER_BASELOAD_FORWARD_CALL_2026'),
    'Enel Trading',
    'POWER_TRADING',
    500.00,  -- 500 MWh
    5.00,    -- $5.00 premium per MWh
    'BUY',
    CURRENT_DATE,
    'trader_user',
    'PENDING_APPROVAL',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Clean up
DROP TABLE trade_instruments;

-- Verify the trades were created
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
WHERE t.trade_id IN ('OPT_FUT_002', 'OPT_FWD_002');

COMMIT;