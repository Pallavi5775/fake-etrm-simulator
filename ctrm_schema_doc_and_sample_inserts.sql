-- CTRM Schema Documentation and Sample Inserts
-- This document describes the schema and provides sample INSERTs for each major table.

-- 1. Instrument Types
-- Table: ctrm.instruments
-- id (bigint, PK)
-- commodity (varchar)
-- currency (varchar)
-- instrument_code (varchar, unique, NOT NULL)
-- instrument_type (varchar, NOT NULL)
-- unit (varchar)

INSERT INTO ctrm.instruments (id, commodity, currency, instrument_code, instrument_type, unit) VALUES
(1, 'GAS', 'USD', 'GASFWD001', 'GAS_FORWARD', 'MMBtu'),
(2, 'POWER', 'EUR', 'PWRFWD001', 'POWER_FORWARD', 'MWh'),
(3, 'OIL', 'USD', 'OILSWP001', 'COMMODITY_SWAP', 'bbl'),
(4, 'GAS', 'USD', 'GASOPT001', 'OPTION', 'MMBtu'),
(5, 'POWER', 'EUR', 'PWRPPA001', 'RENEWABLE_PPA', 'MWh'),
(6, 'FREIGHT', 'USD', 'FRT001', 'FREIGHT', 'MT'),
-- ...
(50, 'GAS', 'USD', 'GASFWD050', 'GAS_FORWARD', 'MMBtu');

-- Subtype: power_forward_instrument
INSERT INTO ctrm.power_forward_instrument (id, start_date, end_date) VALUES
(2, '2026-01-01', '2026-12-31'),
(7, '2026-02-01', '2026-12-31'),
(12, '2026-03-01', '2026-12-31'),
(17, '2026-04-01', '2026-12-31'),
(22, '2026-05-01', '2026-12-31'),
(27, '2026-06-01', '2026-12-31'),
(32, '2026-07-01', '2026-12-31'),
(37, '2026-08-01', '2026-12-31'),
(42, '2026-09-01', '2026-12-31'),
(47, '2026-10-01', '2026-12-31');

-- Subtype: gas_forward_instrument
INSERT INTO ctrm.gas_forward_instrument (id, delivery_date) VALUES
(1, '2026-01-01'),
(6, '2026-01-02'),
(11, '2026-01-03'),
(16, '2026-01-04'),
(21, '2026-01-05'),
(26, '2026-01-06'),
(31, '2026-01-07'),
(36, '2026-01-08'),
(41, '2026-01-09'),
(46, '2026-01-10');

-- Subtype: commodity_option_instrument
INSERT INTO ctrm.commodity_option_instrument (id, expiry_date, option_type, strike_price) VALUES
(4, '2026-06-30', 'CALL', 3.50),
(9, '2026-07-31', 'PUT', 2.80),
(14, '2026-08-31', 'CALL', 4.10),
(19, '2026-09-30', 'PUT', 3.00),
(24, '2026-10-31', 'CALL', 3.75),
(29, '2026-11-30', 'PUT', 2.95),
(34, '2026-12-31', 'CALL', 4.20),
(39, '2027-01-31', 'PUT', 3.10),
(44, '2027-02-28', 'CALL', 3.90),
(49, '2027-03-31', 'PUT', 3.25);

-- Subtype: commodity_swap_instrument
INSERT INTO ctrm.commodity_swap_instrument (id, start_date, end_date, fixed_price, floating_price_index) VALUES
(3, '2026-01-01', '2026-12-31', 75.00, 'ICE'),
(8, '2026-02-01', '2026-12-31', 80.00, 'NYMEX'),
(13, '2026-03-01', '2026-12-31', 78.50, 'ICE'),
(18, '2026-04-01', '2026-12-31', 82.00, 'NYMEX'),
(23, '2026-05-01', '2026-12-31', 77.25, 'ICE'),
(28, '2026-06-01', '2026-12-31', 79.00, 'NYMEX'),
(33, '2026-07-01', '2026-12-31', 81.50, 'ICE'),
(38, '2026-08-01', '2026-12-31', 83.00, 'NYMEX'),
(43, '2026-09-01', '2026-12-31', 76.80, 'ICE'),
(48, '2026-10-01', '2026-12-31', 80.20, 'NYMEX');

-- Subtype: renewableppainstrument
INSERT INTO ctrm.renewableppainstrument (id, forecast_curve, settlement_type, technology) VALUES
(5, 'FCURVE1', 'PHYSICAL', 'SOLAR'),
(10, 'FCURVE2', 'FINANCIAL', 'WIND'),
(15, 'FCURVE3', 'PHYSICAL', 'HYDRO'),
(20, 'FCURVE4', 'FINANCIAL', 'SOLAR'),
(25, 'FCURVE5', 'PHYSICAL', 'WIND'),
(30, 'FCURVE6', 'FINANCIAL', 'HYDRO'),
(35, 'FCURVE7', 'PHYSICAL', 'SOLAR'),
(40, 'FCURVE8', 'FINANCIAL', 'WIND'),
(45, 'FCURVE9', 'PHYSICAL', 'HYDRO'),
(50, 'FCURVE10', 'FINANCIAL', 'SOLAR');

-- 2. Deal Templates
INSERT INTO ctrm.deal_templates (id, auto_approval_allowed, commodity, currency, default_price, default_quantity, instrument_type, mtm_approval_threshold, pricing_model, template_name, unit, instrument_id) VALUES
(1, true, 'GAS', 'USD', 3.25, 10000, 'GAS_FORWARD', 100000, 'BLACK76', 'GAS_TEMPLATE_1', 'MMBtu', 1),
(2, false, 'POWER', 'EUR', 50.00, 5000, 'POWER_FORWARD', 200000, 'DCF', 'POWER_TEMPLATE_1', 'MWh', 2),
(3, true, 'OIL', 'USD', 75.00, 2000, 'COMMODITY_SWAP', 150000, 'DCF', 'OIL_TEMPLATE_1', 'bbl', 3),
(4, false, 'GAS', 'USD', 3.50, 8000, 'OPTION', 120000, 'BLACK76', 'GAS_OPTION_TEMPLATE_1', 'MMBtu', 4),
(5, true, 'POWER', 'EUR', 55.00, 6000, 'RENEWABLE_PPA', 250000, 'RENEWABLE_FORECAST', 'PPA_TEMPLATE_1', 'MWh', 5),
-- ...
(50, true, 'GAS', 'USD', 3.75, 12000, 'GAS_FORWARD', 110000, 'BLACK76', 'GAS_TEMPLATE_50', 'MMBtu', 50);

-- 3. Trade Lifecycle Rules
INSERT INTO ctrm.trade_lifecycle_rules (id, auto, current_status, event_type, max_occurrence, next_status, requires_approval) VALUES
(1, true, 'CREATED', 'CREATED', 1, 'VALIDATED', false),
(2, false, 'VALIDATED', 'AMENDED', 2, 'BOOKED', true),
(3, true, 'BOOKED', 'PRICED', 1, 'PRICED', false),
(4, false, 'PRICED', 'DELIVERED', 1, 'DELIVERED', true),
(5, true, 'DELIVERED', 'INVOICED', 1, 'INVOICED', false),
-- ...
(50, false, 'APPROVED', 'APPROVED', 1, 'SETTLED', true);

-- 4. Lifecycle Rules
INSERT INTO ctrm.lifecycle_rules (id, approval_role, auto_approve, desk, effective_from, effective_to, enabled, event, event_type, from_state, from_status, max_occurrence, name, production_enabled, to_state, to_status, version) VALUES
(1, 'TRADER', true, 'POWER', '2026-01-01', '2026-12-31', true, 'BOOK', 'CREATED', 'CREATED', 'CREATED', 1, 'Rule1', true, 'VALIDATED', 'VALIDATED', 1),
(2, 'RISK', false, 'GAS', '2026-01-01', '2026-12-31', true, 'PRICE', 'PRICED', 'VALIDATED', 'VALIDATED', 2, 'Rule2', false, 'BOOKED', 'BOOKED', 1),
-- ...
(50, 'CFO', true, 'OIL', '2026-01-01', '2026-12-31', true, 'SETTLE', 'SETTLED', 'APPROVED', 'APPROVED', 1, 'Rule50', true, 'SETTLED', 'SETTLED', 1);

-- 5. Trades
INSERT INTO ctrm.trades (id, buy_sell, counterparty, created_at, pending_approval_role, portfolio, price, quantity, status, template_id, trade_id, updated_at, instrument_id) VALUES
(1, 'BUY', 'BP', '2026-01-01 09:00:00', 'TRADER', 'PORT1', 3.25, 10000, 'CREATED', 1, 'T0001', '2026-01-01 09:00:00', 1),
(2, 'SELL', 'SHELL', '2026-01-02 10:00:00', 'RISK', 'PORT2', 50.00, 5000, 'VALIDATED', 2, 'T0002', '2026-01-02 10:00:00', 2),
-- ...
(50, 'BUY', 'BP', '2026-02-19 09:00:00', 'TRADER', 'PORT1', 3.75, 12000, 'APPROVED', 50, 'T0050', '2026-02-19 09:00:00', 50);

-- 6. Forward Curves
INSERT INTO ctrm.forward_curves (id, delivery_date, price, instrument_id) VALUES
(1, '2026-01-01', 3.25, 1),
(2, '2026-01-02', 3.30, 1),
-- ...
(50, '2026-02-19', 4.00, 1);

-- 7. Market Prices
INSERT INTO ctrm.market_prices (id, instrument_code, price) VALUES
(1, 'GASFWD001', 3.25),
(2, 'PWRFWD001', 50.00),
-- ...
(50, 'GASFWD050', 3.75);

-- 8. Portfolio Positions
INSERT INTO ctrm.portfolio_positions (id, net_quantity, portfolio, instrument_id) VALUES
(1, 10000, 'PORT1', 1),
(2, 5000, 'PORT2', 2),
-- ...
(50, 12000, 'PORT1', 50);

-- 9. Credit Limits
INSERT INTO ctrm.credit_limit (id, counterparty, limit_amount) VALUES
(1, 'BP', 1000000),
(2, 'SHELL', 2000000),
-- ...
(50, 'BP', 1500000);

-- 10. Valuation History
INSERT INTO ctrm.valuation_history (id, mtm, valuation_date, trade_id) VALUES
(1, 10000, '2026-01-01', 1),
(2, 20000, '2026-01-02', 2),
-- ...
(50, 15000, '2026-02-19', 50);

-- 11. Trade Events
INSERT INTO ctrm.trade_events (id, created_at, event_type, source, triggered_by, trade_id) VALUES
(1, '2026-01-01 09:00:00', 'CREATED', 'SYSTEM', 'TRADER', 1),
(2, '2026-01-02 10:00:00', 'VALIDATED', 'SYSTEM', 'RISK', 2),
-- ...
(50, '2026-02-19 09:00:00', 'APPROVED', 'SYSTEM', 'TRADER', 50);

-- Relationships:
-- - instruments is the parent for all instrument subtypes and is referenced by trades, deal_templates, forward_curves, portfolio_positions, etc.
-- - deal_templates references instruments.
-- - trades references instruments and deal_templates.
-- - forward_curves references instruments.
-- - portfolio_positions references instruments.
-- - valuation_history and trade_events reference trades.
