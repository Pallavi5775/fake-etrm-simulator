-- ============================================================================
-- CTRM SIMULATOR - OPTIONS MARKET DATA TEMPLATES
-- ============================================================================
-- This script provides sample data for options on futures and forwards
-- across energy, metals, and agriculture commodities
-- ============================================================================

-- Use the ctrm schema
SET search_path TO ctrm;

-- ============================================================================
-- 1. COMMODITIES (Referenced by instruments)
-- ============================================================================

INSERT INTO commodity (name) VALUES
('WTI Crude Oil'),
('Brent Crude Oil'),
('Natural Gas'),
('Power Baseload'),
('Gold'),
('Copper'),
('Corn'),
('Soybeans')
ON CONFLICT (name) DO NOTHING;

-- ============================================================================
-- 2. OPTION INSTRUMENTS
-- ============================================================================

-- Get commodity IDs for reference
CREATE TEMP TABLE temp_commodities AS
SELECT id, name FROM commodity;

-- Energy - Oil Futures Options
INSERT INTO instruments (instrument_code, commodity_id, currency, unit, instrument_type) VALUES
('WTI_FUTURES_CALL_2026', (SELECT id FROM temp_commodities WHERE name = 'WTI Crude Oil'), 'USD', 'BBL', 'OPTION'),
('WTI_FUTURES_PUT_2026', (SELECT id FROM temp_commodities WHERE name = 'WTI Crude Oil'), 'USD', 'BBL', 'OPTION'),
('BRENT_FUTURES_CALL_2026', (SELECT id FROM temp_commodities WHERE name = 'Brent Crude Oil'), 'USD', 'BBL', 'OPTION'),
('BRENT_FUTURES_PUT_2026', (SELECT id FROM temp_commodities WHERE name = 'Brent Crude Oil'), 'USD', 'BBL', 'OPTION'),

-- Energy - Gas Futures Options
('NG_FUTURES_CALL_Q1_2026', (SELECT id FROM temp_commodities WHERE name = 'Natural Gas'), 'USD', 'MMBTU', 'OPTION'),
('NG_FUTURES_PUT_Q1_2026', (SELECT id FROM temp_commodities WHERE name = 'Natural Gas'), 'USD', 'MMBTU', 'OPTION'),

-- Energy - Power Forward Options (OTC)
('POWER_BASELOAD_FORWARD_CALL_2026', (SELECT id FROM temp_commodities WHERE name = 'Power Baseload'), 'USD', 'MWH', 'OPTION'),
('POWER_BASELOAD_FORWARD_PUT_2026', (SELECT id FROM temp_commodities WHERE name = 'Power Baseload'), 'USD', 'MWH', 'OPTION'),

-- Metals - Futures Options
('GOLD_FUTURES_CALL_2026', (SELECT id FROM temp_commodities WHERE name = 'Gold'), 'USD', 'OZ', 'OPTION'),
('GOLD_FUTURES_PUT_2026', (SELECT id FROM temp_commodities WHERE name = 'Gold'), 'USD', 'OZ', 'OPTION'),
('COPPER_FUTURES_CALL_2026', (SELECT id FROM temp_commodities WHERE name = 'Copper'), 'USD', 'LB', 'OPTION'),
('COPPER_FUTURES_PUT_2026', (SELECT id FROM temp_commodities WHERE name = 'Copper'), 'USD', 'LB', 'OPTION'),

-- Agriculture - Futures Options
('CORN_FUTURES_CALL_2026', (SELECT id FROM temp_commodities WHERE name = 'Corn'), 'USD', 'BU', 'OPTION'),
('CORN_FUTURES_PUT_2026', (SELECT id FROM temp_commodities WHERE name = 'Corn'), 'USD', 'BU', 'OPTION'),
('SOYBEAN_FUTURES_CALL_2026', (SELECT id FROM temp_commodities WHERE name = 'Soybeans'), 'USD', 'BU', 'OPTION'),
('SOYBEAN_FUTURES_PUT_2026', (SELECT id FROM temp_commodities WHERE name = 'Soybeans'), 'USD', 'BU', 'OPTION');

-- Insert CommodityOptionInstrument specific data
INSERT INTO commodity_option_instrument (id, strike_price, expiry_date, option_type, underlying_type) VALUES
(1, 80.00, '2026-12-31', 'CALL', 'FUTURES'),  -- WTI_FUTURES_CALL_2026
(2, 80.00, '2026-12-31', 'PUT', 'FUTURES'),   -- WTI_FUTURES_PUT_2026
(3, 82.00, '2026-12-31', 'CALL', 'FUTURES'),  -- BRENT_FUTURES_CALL_2026
(4, 82.00, '2026-12-31', 'PUT', 'FUTURES'),   -- BRENT_FUTURES_PUT_2026
(5, 3.50, '2026-03-31', 'CALL', 'FUTURES'),   -- NG_FUTURES_CALL_Q1_2026
(6, 3.50, '2026-03-31', 'PUT', 'FUTURES'),    -- NG_FUTURES_PUT_Q1_2026
(7, 50.00, '2026-12-31', 'CALL', 'FORWARD'),  -- POWER_BASELOAD_FORWARD_CALL_2026
(8, 50.00, '2026-12-31', 'PUT', 'FORWARD'),   -- POWER_BASELOAD_FORWARD_PUT_2026
(9, 1900.00, '2026-12-31', 'CALL', 'FUTURES'), -- GOLD_FUTURES_CALL_2026
(10, 1900.00, '2026-12-31', 'PUT', 'FUTURES'), -- GOLD_FUTURES_PUT_2026
(11, 4.00, '2026-12-31', 'CALL', 'FUTURES'),  -- COPPER_FUTURES_CALL_2026
(12, 4.00, '2026-12-31', 'PUT', 'FUTURES'),   -- COPPER_FUTURES_PUT_2026
(13, 5.00, '2026-12-31', 'CALL', 'FUTURES'),  -- CORN_FUTURES_CALL_2026
(14, 5.00, '2026-12-31', 'PUT', 'FUTURES'),   -- CORN_FUTURES_PUT_2026
(15, 13.00, '2026-12-31', 'CALL', 'FUTURES'), -- SOYBEAN_FUTURES_CALL_2026
(16, 13.00, '2026-12-31', 'PUT', 'FUTURES');  -- SOYBEAN_FUTURES_PUT_2026


-- ============================================================================
-- 1. OPTION FORWARD CURVES
-- ============================================================================

-- Energy - Oil Futures Options
INSERT INTO option_forward_curves (instrument_code, delivery_date, forward_price, curve_date) VALUES
('WTI_FUTURES_CALL_2026', '2026-12-31', 75.50, '2026-01-05'),
('WTI_FUTURES_PUT_2026', '2026-12-31', 75.50, '2026-01-05'),
('BRENT_FUTURES_CALL_2026', '2026-12-31', 78.25, '2026-01-05'),
('BRENT_FUTURES_PUT_2026', '2026-12-31', 78.25, '2026-01-05'),

-- Energy - Gas Futures Options
('NG_FUTURES_CALL_Q1_2026', '2026-03-31', 3.25, '2026-01-05'),
('NG_FUTURES_PUT_Q1_2026', '2026-03-31', 3.25, '2026-01-05'),

-- Energy - Power Forward Options (OTC)
('POWER_BASELOAD_FORWARD_CALL_2026', '2026-12-31', 45.00, '2026-01-05'),
('POWER_BASELOAD_FORWARD_PUT_2026', '2026-12-31', 45.00, '2026-01-05'),

-- Metals - Futures Options
('GOLD_FUTURES_CALL_2026', '2026-12-31', 1850.00, '2026-01-05'),
('GOLD_FUTURES_PUT_2026', '2026-12-31', 1850.00, '2026-01-05'),
('COPPER_FUTURES_CALL_2026', '2026-12-31', 3.85, '2026-01-05'),
('COPPER_FUTURES_PUT_2026', '2026-12-31', 3.85, '2026-01-05'),

-- Agriculture - Futures Options
('CORN_FUTURES_CALL_2026', '2026-12-31', 4.75, '2026-01-05'),
('CORN_FUTURES_PUT_2026', '2026-12-31', 4.75, '2026-01-05'),
('SOYBEAN_FUTURES_CALL_2026', '2026-12-31', 12.50, '2026-01-05'),
('SOYBEAN_FUTURES_PUT_2026', '2026-12-31', 12.50, '2026-01-05');

-- ============================================================================
-- 2. OPTION VOLATILITY
-- ============================================================================

-- Energy Options Volatility (higher for futures, moderate for forwards)
INSERT INTO option_volatility (instrument_code, date, value) VALUES
('WTI_FUTURES_CALL_2026', '2026-01-05', 0.25),     -- 25% vol for oil futures
('WTI_FUTURES_PUT_2026', '2026-01-05', 0.25),
('BRENT_FUTURES_CALL_2026', '2026-01-05', 0.22),   -- 22% vol for Brent
('BRENT_FUTURES_PUT_2026', '2026-01-05', 0.22),
('NG_FUTURES_CALL_Q1_2026', '2026-01-05', 0.35),   -- 35% vol for gas (more volatile)
('NG_FUTURES_PUT_Q1_2026', '2026-01-05', 0.35),

-- Power Forward Options (lower volatility for longer-term contracts)
('POWER_BASELOAD_FORWARD_CALL_2026', '2026-01-05', 0.15),
('POWER_BASELOAD_FORWARD_PUT_2026', '2026-01-05', 0.15),

-- Metals Options Volatility
('GOLD_FUTURES_CALL_2026', '2026-01-05', 0.18),    -- 18% vol for gold
('GOLD_FUTURES_PUT_2026', '2026-01-05', 0.18),
('COPPER_FUTURES_CALL_2026', '2026-01-05', 0.20),  -- 20% vol for copper
('COPPER_FUTURES_PUT_2026', '2026-01-05', 0.20),

-- Agriculture Options Volatility (seasonal factors)
('CORN_FUTURES_CALL_2026', '2026-01-05', 0.28),    -- 28% vol for corn
('CORN_FUTURES_PUT_2026', '2026-01-05', 0.28),
('SOYBEAN_FUTURES_CALL_2026', '2026-01-05', 0.24), -- 24% vol for soybeans
('SOYBEAN_FUTURES_PUT_2026', '2026-01-05', 0.24);

-- ============================================================================
-- 3. OPTION YIELD CURVES
-- ============================================================================

-- Currency-based discount curves for options
INSERT INTO option_yield_curves (curve_name, date, yield) VALUES
('USD_DISCOUNT', '2026-01-05', 0.0425),  -- 4.25% USD discount rate
('EUR_DISCOUNT', '2026-01-05', 0.0375),  -- 3.75% EUR discount rate
('GBP_DISCOUNT', '2026-01-05', 0.0450),  -- 4.50% GBP discount rate

-- Fallback rates for different maturities
('USD_DISCOUNT', '2026-12-31', 0.0425),
('EUR_DISCOUNT', '2026-12-31', 0.0375),
('GBP_DISCOUNT', '2026-12-31', 0.0450);

-- ============================================================================
-- 4. SAMPLE OPTION INSTRUMENTS
-- ============================================================================

-- Note: These would typically be created through the application UI/API
-- but here's the structure for reference:

-- Futures-based options (use Black model)
-- CommodityOptionInstrument:
--   instrumentCode: "WTI_FUTURES_CALL_2026"
--   strikePrice: 80.00
--   expiryDate: 2026-12-31
--   optionType: "CALL"
--   underlyingType: "FUTURES"
--   currency: "USD"

-- Forward-based options (use Black-76 model)
-- CommodityOptionInstrument:
--   instrumentCode: "POWER_BASELOAD_FORWARD_CALL_2026"
--   strikePrice: 50.00
--   expiryDate: 2026-12-31
--   optionType: "CALL"
--   underlyingType: "FORWARD"
--   currency: "USD"

COMMIT;

-- ============================================================================
-- VERIFICATION QUERIES
-- ============================================================================

-- Check option forward curves
SELECT COUNT(*) as forward_curves_count FROM option_forward_curves;

-- Check option volatility
SELECT COUNT(*) as volatility_count FROM option_volatility;

-- Check option yield curves
SELECT COUNT(*) as yield_curves_count FROM option_yield_curves;

-- Sample query: Get all WTI options data
SELECT
    fc.instrument_code,
    fc.forward_price,
    v.value as volatility,
    yc.yield as discount_rate
FROM option_forward_curves fc
JOIN option_volatility v ON fc.instrument_code = v.instrument_code
JOIN option_yield_curves yc ON yc.curve_name = 'USD_DISCOUNT'
WHERE fc.instrument_code LIKE 'WTI_FUTURES%'
  AND fc.curve_date = '2026-01-05'
  AND v.date = '2026-01-05'
  AND yc.date = '2026-01-05';