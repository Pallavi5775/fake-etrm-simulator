-- Database Truncation Script for CTRA Simulator
-- This script truncates all tables in the correct order to respect foreign key constraints

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
-- Add other sequences as needed

COMMIT;