-- Complete Database Setup Script for CTRA Simulator
-- This script truncates all tables, resets sequences, inserts commodities, and then instruments

-- Disable foreign key checks
SET session_replication_role = 'replica';

-- Truncate tables in dependency order (child tables first, then parent tables)

-- Pricing and valuation tables
TRUNCATE TABLE ctrm.valuation_result CASCADE;
TRUNCATE TABLE ctrm.valuation_run CASCADE;
TRUNCATE TABLE ctrm.scenario_result CASCADE;
TRUNCATE TABLE ctrm.pnl_explain CASCADE;

-- Options data tables
TRUNCATE TABLE ctrm.option_forward_curves CASCADE;
TRUNCATE TABLE ctrm.option_volatility CASCADE;
TRUNCATE TABLE ctrm.option_yield_curves CASCADE;

-- Market data tables
TRUNCATE TABLE ctrm.market_prices CASCADE;
TRUNCATE TABLE ctrm.price_curve CASCADE;
TRUNCATE TABLE ctrm.yield_curve CASCADE;
TRUNCATE TABLE ctrm.volatility_surface CASCADE;
TRUNCATE TABLE ctrm.forecast_price CASCADE;
TRUNCATE TABLE ctrm.weather_data CASCADE;
TRUNCATE TABLE ctrm.generation_forecast CASCADE;

-- Trade-related tables
TRUNCATE TABLE ctrm.trade_legs CASCADE;
TRUNCATE TABLE ctrm.trades CASCADE;
TRUNCATE TABLE ctrm.trade_version CASCADE;
TRUNCATE TABLE ctrm.trade_events CASCADE;

-- Instrument tables (be careful with inheritance)
TRUNCATE TABLE ctrm.commodity_option_instrument CASCADE;
TRUNCATE TABLE ctrm.power_forward_instrument CASCADE;
TRUNCATE TABLE ctrm.gas_forward_instrument CASCADE;
TRUNCATE TABLE ctrm.renewableppainstrument CASCADE;
TRUNCATE TABLE ctrm.commodity_swap_instrument CASCADE;
TRUNCATE TABLE ctrm.freight_instrument CASCADE;
TRUNCATE TABLE ctrm.instruments CASCADE;

-- Reference data
TRUNCATE TABLE ctrm.commodity CASCADE;
TRUNCATE TABLE ctrm.counterparties CASCADE;
TRUNCATE TABLE ctrm.portfolios CASCADE;

-- User and security tables
TRUNCATE TABLE ctrm.users CASCADE;
TRUNCATE TABLE ctrm.roles CASCADE;

-- Risk and credit tables
TRUNCATE TABLE ctrm.credit_limit CASCADE;

-- Re-enable foreign key checks
SET session_replication_role = 'origin';

-- Reset sequences to start from 1
ALTER SEQUENCE IF EXISTS ctrm.instruments_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS ctrm.commodities_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS ctrm.trades_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS ctrm.users_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS ctrm.valuation_result_result_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS ctrm.valuation_run_id_seq RESTART WITH 1;

-- Insert commodities first
INSERT INTO ctrm.commodity (name) VALUES
('WTI Crude Oil'),
('Brent Crude Oil'),
('Natural Gas'),
('Power Baseload'),
('Gold'),
('Copper'),
('Corn'),
('Soybeans');

-- Create temporary table for commodity references
CREATE TEMP TABLE temp_commodities AS
SELECT id, name FROM ctrm.commodity;

-- Energy - Oil Futures Options
INSERT INTO ctrm.instruments (instrument_code, commodity_id, currency, unit, instrument_type) VALUES
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

-- Clean up temporary table
DROP TABLE temp_commodities;

COMMIT;