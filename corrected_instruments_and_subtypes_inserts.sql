ALTER TABLE ctrm.instruments ADD COLUMN commodity character varying(255);
-- Optionally, if you want to set NOT NULL and a default, use:
-- ALTER TABLE ctrm.instruments ALTER COLUMN commodity SET NOT NULL;
-- ALTER TABLE ctrm.instruments ALTER COLUMN commodity SET DEFAULT '';


-- 1. Insert 50 instruments, covering all ids referenced by subtypes
INSERT INTO ctrm.instruments (id, commodity, currency, instrument_code, instrument_type, unit)
VALUES
(1, 'GAS', 'USD', 'GASFWD001', 'GAS_FORWARD', 'MMBtu'),
(2, 'POWER', 'EUR', 'PWRFWD001', 'POWER_FORWARD', 'MWh'),
(3, 'OIL', 'USD', 'OILSWP001', 'COMMODITY_SWAP', 'bbl'),
(4, 'GAS', 'USD', 'GASOPT001', 'OPTION', 'MMBtu'),
(5, 'POWER', 'EUR', 'PWRPPA001', 'RENEWABLE_PPA', 'MWh'),
(6, 'FREIGHT', 'USD', 'FRT001', 'FREIGHT', 'MT'),
(7, 'POWER', 'EUR', 'PWRFWD002', 'POWER_FORWARD', 'MWh'),
(8, 'OIL', 'USD', 'OILSWP002', 'COMMODITY_SWAP', 'bbl'),
(9, 'GAS', 'USD', 'GASOPT002', 'OPTION', 'MMBtu'),
(10, 'POWER', 'EUR', 'PWRPPA002', 'RENEWABLE_PPA', 'MWh'),
(11, 'GAS', 'USD', 'GASFWD002', 'GAS_FORWARD', 'MMBtu'),
(12, 'POWER', 'EUR', 'PWRFWD003', 'POWER_FORWARD', 'MWh'),
(13, 'OIL', 'USD', 'OILSWP003', 'COMMODITY_SWAP', 'bbl'),
(14, 'GAS', 'USD', 'GASOPT003', 'OPTION', 'MMBtu'),
(15, 'POWER', 'EUR', 'PWRPPA003', 'RENEWABLE_PPA', 'MWh'),
(16, 'GAS', 'USD', 'GASFWD003', 'GAS_FORWARD', 'MMBtu'),
(17, 'POWER', 'EUR', 'PWRFWD004', 'POWER_FORWARD', 'MWh'),
(18, 'OIL', 'USD', 'OILSWP004', 'COMMODITY_SWAP', 'bbl'),
(19, 'GAS', 'USD', 'GASOPT004', 'OPTION', 'MMBtu'),
(20, 'POWER', 'EUR', 'PWRPPA004', 'RENEWABLE_PPA', 'MWh'),
(21, 'GAS', 'USD', 'GASFWD004', 'GAS_FORWARD', 'MMBtu'),
(22, 'POWER', 'EUR', 'PWRFWD005', 'POWER_FORWARD', 'MWh'),
(23, 'OIL', 'USD', 'OILSWP005', 'COMMODITY_SWAP', 'bbl'),
(24, 'GAS', 'USD', 'GASOPT005', 'OPTION', 'MMBtu'),
(25, 'POWER', 'EUR', 'PWRPPA005', 'RENEWABLE_PPA', 'MWh'),
(26, 'GAS', 'USD', 'GASFWD005', 'GAS_FORWARD', 'MMBtu'),
(27, 'POWER', 'EUR', 'PWRFWD006', 'POWER_FORWARD', 'MWh'),
(28, 'OIL', 'USD', 'OILSWP006', 'COMMODITY_SWAP', 'bbl'),
(29, 'GAS', 'USD', 'GASOPT006', 'OPTION', 'MMBtu'),
(30, 'POWER', 'EUR', 'PWRPPA006', 'RENEWABLE_PPA', 'MWh'),
(31, 'GAS', 'USD', 'GASFWD006', 'GAS_FORWARD', 'MMBtu'),
(32, 'POWER', 'EUR', 'PWRFWD007', 'POWER_FORWARD', 'MWh'),
(33, 'OIL', 'USD', 'OILSWP007', 'COMMODITY_SWAP', 'bbl'),
(34, 'GAS', 'USD', 'GASOPT007', 'OPTION', 'MMBtu'),
(35, 'POWER', 'EUR', 'PWRPPA007', 'RENEWABLE_PPA', 'MWh'),
(36, 'GAS', 'USD', 'GASFWD007', 'GAS_FORWARD', 'MMBtu'),
(37, 'POWER', 'EUR', 'PWRFWD008', 'POWER_FORWARD', 'MWh'),
(38, 'OIL', 'USD', 'OILSWP008', 'COMMODITY_SWAP', 'bbl'),
(39, 'GAS', 'USD', 'GASOPT008', 'OPTION', 'MMBtu'),
(40, 'POWER', 'EUR', 'PWRPPA008', 'RENEWABLE_PPA', 'MWh'),
(41, 'GAS', 'USD', 'GASFWD008', 'GAS_FORWARD', 'MMBtu'),
(42, 'POWER', 'EUR', 'PWRFWD009', 'POWER_FORWARD', 'MWh'),
(43, 'OIL', 'USD', 'OILSWP009', 'COMMODITY_SWAP', 'bbl'),
(44, 'GAS', 'USD', 'GASOPT009', 'OPTION', 'MMBtu'),
(45, 'POWER', 'EUR', 'PWRPPA009', 'RENEWABLE_PPA', 'MWh'),
(46, 'GAS', 'USD', 'GASFWD009', 'GAS_FORWARD', 'MMBtu'),
(47, 'POWER', 'EUR', 'PWRFWD010', 'POWER_FORWARD', 'MWh'),
(48, 'OIL', 'USD', 'OILSWP010', 'COMMODITY_SWAP', 'bbl'),
(49, 'GAS', 'USD', 'GASOPT010', 'OPTION', 'MMBtu'),
(50, 'POWER', 'EUR', 'PWRPPA010', 'RENEWABLE_PPA', 'MWh');

-- 2. Now insert into subtypes (all ids exist in instruments)
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


-- 3. Insert 50 deal_templates referencing the above instruments
INSERT INTO ctrm.deal_templates (
	id, auto_approval_allowed, commodity, currency, default_price, default_quantity, instrument_type, mtm_approval_threshold, pricing_model, template_name, unit, instrument_id
) VALUES
(1, true, 'GAS', 'USD', 3.25, 10000, 'GAS_FORWARD', 100000, 'BLACK76', 'GAS_TEMPLATE_1', 'MMBtu', 1),
(2, false, 'POWER', 'EUR', 50.00, 5000, 'POWER_FORWARD', 200000, 'DCF', 'POWER_TEMPLATE_1', 'MWh', 2),
(3, true, 'OIL', 'USD', 75.00, 2000, 'COMMODITY_SWAP', 150000, 'DCF', 'OIL_TEMPLATE_1', 'bbl', 3),
(4, false, 'GAS', 'USD', 3.50, 8000, 'OPTION', 120000, 'BLACK76', 'GAS_OPTION_TEMPLATE_1', 'MMBtu', 4),
(5, true, 'POWER', 'EUR', 55.00, 6000, 'RENEWABLE_PPA', 250000, 'RENEWABLE_FORECAST', 'PPA_TEMPLATE_1', 'MWh', 5),
(6, true, 'FREIGHT', 'USD', 20.00, 1000, 'FREIGHT', 50000, 'DCF', 'FREIGHT_TEMPLATE_1', 'MT', 6),
(7, false, 'POWER', 'EUR', 51.00, 5100, 'POWER_FORWARD', 201000, 'DCF', 'POWER_TEMPLATE_2', 'MWh', 7),
(8, true, 'OIL', 'USD', 76.00, 2100, 'COMMODITY_SWAP', 151000, 'DCF', 'OIL_TEMPLATE_2', 'bbl', 8),
(9, false, 'GAS', 'USD', 3.60, 8100, 'OPTION', 121000, 'BLACK76', 'GAS_OPTION_TEMPLATE_2', 'MMBtu', 9),
(10, true, 'POWER', 'EUR', 56.00, 6100, 'RENEWABLE_PPA', 251000, 'RENEWABLE_FORECAST', 'PPA_TEMPLATE_2', 'MWh', 10),
(11, true, 'GAS', 'USD', 3.27, 10010, 'GAS_FORWARD', 100100, 'BLACK76', 'GAS_TEMPLATE_11', 'MMBtu', 11),
(12, false, 'POWER', 'EUR', 52.00, 5200, 'POWER_FORWARD', 202000, 'DCF', 'POWER_TEMPLATE_3', 'MWh', 12),
(13, true, 'OIL', 'USD', 77.00, 2200, 'COMMODITY_SWAP', 152000, 'DCF', 'OIL_TEMPLATE_3', 'bbl', 13),
(14, false, 'GAS', 'USD', 3.70, 8200, 'OPTION', 122000, 'BLACK76', 'GAS_OPTION_TEMPLATE_3', 'MMBtu', 14),
(15, true, 'POWER', 'EUR', 57.00, 6200, 'RENEWABLE_PPA', 252000, 'RENEWABLE_FORECAST', 'PPA_TEMPLATE_3', 'MWh', 15),
(16, true, 'GAS', 'USD', 3.29, 10020, 'GAS_FORWARD', 100200, 'BLACK76', 'GAS_TEMPLATE_16', 'MMBtu', 16),
(17, false, 'POWER', 'EUR', 53.00, 5300, 'POWER_FORWARD', 203000, 'DCF', 'POWER_TEMPLATE_4', 'MWh', 17),
(18, true, 'OIL', 'USD', 78.00, 2300, 'COMMODITY_SWAP', 153000, 'DCF', 'OIL_TEMPLATE_4', 'bbl', 18),
(19, false, 'GAS', 'USD', 3.80, 8300, 'OPTION', 123000, 'BLACK76', 'GAS_OPTION_TEMPLATE_4', 'MMBtu', 19),
(20, true, 'POWER', 'EUR', 58.00, 6300, 'RENEWABLE_PPA', 253000, 'RENEWABLE_FORECAST', 'PPA_TEMPLATE_4', 'MWh', 20),
(21, true, 'GAS', 'USD', 3.31, 10030, 'GAS_FORWARD', 100300, 'BLACK76', 'GAS_TEMPLATE_21', 'MMBtu', 21),
(22, false, 'POWER', 'EUR', 54.00, 5400, 'POWER_FORWARD', 204000, 'DCF', 'POWER_TEMPLATE_5', 'MWh', 22),
(23, true, 'OIL', 'USD', 79.00, 2400, 'COMMODITY_SWAP', 154000, 'DCF', 'OIL_TEMPLATE_5', 'bbl', 23),
(24, false, 'GAS', 'USD', 3.90, 8400, 'OPTION', 124000, 'BLACK76', 'GAS_OPTION_TEMPLATE_5', 'MMBtu', 24),
(25, true, 'POWER', 'EUR', 59.00, 6400, 'RENEWABLE_PPA', 254000, 'RENEWABLE_FORECAST', 'PPA_TEMPLATE_5', 'MWh', 25),
(26, true, 'GAS', 'USD', 3.33, 10040, 'GAS_FORWARD', 100400, 'BLACK76', 'GAS_TEMPLATE_26', 'MMBtu', 26),
(27, false, 'POWER', 'EUR', 55.00, 5500, 'POWER_FORWARD', 205000, 'DCF', 'POWER_TEMPLATE_6', 'MWh', 27),
(28, true, 'OIL', 'USD', 80.00, 2500, 'COMMODITY_SWAP', 155000, 'DCF', 'OIL_TEMPLATE_6', 'bbl', 28),
(29, false, 'GAS', 'USD', 4.00, 8500, 'OPTION', 125000, 'BLACK76', 'GAS_OPTION_TEMPLATE_6', 'MMBtu', 29),
(30, true, 'POWER', 'EUR', 60.00, 6500, 'RENEWABLE_PPA', 255000, 'RENEWABLE_FORECAST', 'PPA_TEMPLATE_6', 'MWh', 30),
(31, true, 'GAS', 'USD', 3.35, 10050, 'GAS_FORWARD', 100500, 'BLACK76', 'GAS_TEMPLATE_31', 'MMBtu', 31),
(32, false, 'POWER', 'EUR', 56.00, 5600, 'POWER_FORWARD', 206000, 'DCF', 'POWER_TEMPLATE_7', 'MWh', 32),
(33, true, 'OIL', 'USD', 81.00, 2600, 'COMMODITY_SWAP', 156000, 'DCF', 'OIL_TEMPLATE_7', 'bbl', 33),
(34, false, 'GAS', 'USD', 4.10, 8600, 'OPTION', 126000, 'BLACK76', 'GAS_OPTION_TEMPLATE_7', 'MMBtu', 34),
(35, true, 'POWER', 'EUR', 61.00, 6600, 'RENEWABLE_PPA', 256000, 'RENEWABLE_FORECAST', 'PPA_TEMPLATE_7', 'MWh', 35),
(36, true, 'GAS', 'USD', 3.37, 10060, 'GAS_FORWARD', 100600, 'BLACK76', 'GAS_TEMPLATE_36', 'MMBtu', 36),
(37, false, 'POWER', 'EUR', 57.00, 5700, 'POWER_FORWARD', 207000, 'DCF', 'POWER_TEMPLATE_8', 'MWh', 37),
(38, true, 'OIL', 'USD', 82.00, 2700, 'COMMODITY_SWAP', 157000, 'DCF', 'OIL_TEMPLATE_8', 'bbl', 38),
(39, false, 'GAS', 'USD', 4.20, 8700, 'OPTION', 127000, 'BLACK76', 'GAS_OPTION_TEMPLATE_8', 'MMBtu', 39),
(40, true, 'POWER', 'EUR', 62.00, 6700, 'RENEWABLE_PPA', 257000, 'RENEWABLE_FORECAST', 'PPA_TEMPLATE_8', 'MWh', 40),
(41, true, 'GAS', 'USD', 3.39, 10070, 'GAS_FORWARD', 100700, 'BLACK76', 'GAS_TEMPLATE_41', 'MMBtu', 41),
(42, false, 'POWER', 'EUR', 58.00, 5800, 'POWER_FORWARD', 208000, 'DCF', 'POWER_TEMPLATE_9', 'MWh', 42),
(43, true, 'OIL', 'USD', 83.00, 2800, 'COMMODITY_SWAP', 158000, 'DCF', 'OIL_TEMPLATE_9', 'bbl', 43),
(44, false, 'GAS', 'USD', 4.30, 8800, 'OPTION', 128000, 'BLACK76', 'GAS_OPTION_TEMPLATE_9', 'MMBtu', 44),
(45, true, 'POWER', 'EUR', 63.00, 6800, 'RENEWABLE_PPA', 258000, 'RENEWABLE_FORECAST', 'PPA_TEMPLATE_9', 'MWh', 45),
(46, true, 'GAS', 'USD', 3.41, 10080, 'GAS_FORWARD', 100800, 'BLACK76', 'GAS_TEMPLATE_46', 'MMBtu', 46),
(47, false, 'POWER', 'EUR', 59.00, 5900, 'POWER_FORWARD', 209000, 'DCF', 'POWER_TEMPLATE_10', 'MWh', 47),
(48, true, 'OIL', 'USD', 84.00, 2900, 'COMMODITY_SWAP', 159000, 'DCF', 'OIL_TEMPLATE_10', 'bbl', 48),
(49, false, 'GAS', 'USD', 4.40, 8900, 'OPTION', 129000, 'BLACK76', 'GAS_OPTION_TEMPLATE_10', 'MMBtu', 49),
(50, true, 'POWER', 'EUR', 64.00, 6900, 'RENEWABLE_PPA', 259000, 'RENEWABLE_FORECAST', 'PPA_TEMPLATE_10', 'MWh', 50);




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
INSERT INTO ctrm.forward_curve (id, delivery_date, price, instrument_id) VALUES
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
