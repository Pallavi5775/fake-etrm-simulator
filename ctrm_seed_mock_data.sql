-- ============================================================================
-- CTRM SIMULATOR SEED DATA
-- Comprehensive sample data for 6 months (Jan-Jun 2026)
-- Includes: Instruments, Counterparties, Portfolios, Risk Limits, Credit Limits,
-- Trades, Multi-Trades, Forward Curves
-- ============================================================================

-- ============================================================================
-- 1. INSTRUMENTS
-- ============================================================================
-- 50 instruments across commodities
INSERT INTO ctrm.instruments (id, commodity, currency, instrument_code, instrument_type, unit) VALUES
(1, 'POWER', 'EUR', 'PWR-FWD-Q1-26', 'POWER_FORWARD', 'MWh'),
(2, 'POWER', 'EUR', 'PWR-FWD-Q2-26', 'POWER_FORWARD', 'MWh'),
(3, 'POWER', 'EUR', 'PWR-FWD-Q3-26', 'POWER_FORWARD', 'MWh'),
(4, 'POWER', 'EUR', 'PWR-FWD-Q4-26', 'POWER_FORWARD', 'MWh'),
(5, 'GAS', 'USD', 'GAS-FWD-JAN-26', 'GAS_FORWARD', 'MMBtu'),
(6, 'GAS', 'USD', 'GAS-FWD-FEB-26', 'GAS_FORWARD', 'MMBtu'),
(7, 'GAS', 'USD', 'GAS-FWD-MAR-26', 'GAS_FORWARD', 'MMBtu'),
(8, 'GAS', 'USD', 'GAS-FWD-APR-26', 'GAS_FORWARD', 'MMBtu'),
(9, 'GAS', 'USD', 'GAS-FWD-MAY-26', 'GAS_FORWARD', 'MMBtu'),
(10, 'GAS', 'USD', 'GAS-FWD-JUN-26', 'GAS_FORWARD', 'MMBtu'),
(11, 'OIL', 'USD', 'OIL-SWAP-26', 'COMMODITY_SWAP', 'bbl'),
(12, 'COAL', 'USD', 'COAL-FWD-26', 'COMMODITY_FORWARD', 'MT'),
(13, 'POWER', 'EUR', 'PWR-OPT-CALL-50', 'OPTION', 'MWh'),
(14, 'GAS', 'USD', 'GAS-OPT-PUT-5', 'OPTION', 'MMBtu'),
(15, 'POWER', 'EUR', 'PWR-PPA-SOLAR', 'RENEWABLE_PPA', 'MWh'),
(16, 'POWER', 'EUR', 'PWR-PPA-WIND', 'RENEWABLE_PPA', 'MWh'),
(17, 'FREIGHT', 'USD', 'FRT-BALTIC', 'FREIGHT', 'MT'),
(18, 'POWER', 'EUR', 'PWR-FWD-Q1-27', 'POWER_FORWARD', 'MWh'),
(19, 'GAS', 'USD', 'GAS-FWD-JUL-26', 'GAS_FORWARD', 'MMBtu'),
(20, 'OIL', 'USD', 'OIL-OPT-60', 'OPTION', 'bbl'),
(21, 'POWER', 'EUR', 'PWR-FWD-Q2-27', 'POWER_FORWARD', 'MWh'),
(22, 'GAS', 'USD', 'GAS-FWD-AUG-26', 'GAS_FORWARD', 'MMBtu'),
(23, 'COAL', 'USD', 'COAL-SWAP-26', 'COMMODITY_SWAP', 'MT'),
(24, 'POWER', 'EUR', 'PWR-OPT-PUT-45', 'OPTION', 'MWh'),
(25, 'GAS', 'USD', 'GAS-OPT-CALL-6', 'OPTION', 'MMBtu'),
(26, 'POWER', 'EUR', 'PWR-PPA-HYDRO', 'RENEWABLE_PPA', 'MWh'),
(27, 'FREIGHT', 'USD', 'FRT-PANAMAX', 'FREIGHT', 'MT'),
(28, 'POWER', 'EUR', 'PWR-FWD-Q3-27', 'POWER_FORWARD', 'MWh'),
(29, 'GAS', 'USD', 'GAS-FWD-SEP-26', 'GAS_FORWARD', 'MMBtu'),
(30, 'OIL', 'USD', 'OIL-FWD-26', 'COMMODITY_FORWARD', 'bbl'),
(31, 'POWER', 'EUR', 'PWR-FWD-Q4-27', 'POWER_FORWARD', 'MWh'),
(32, 'GAS', 'USD', 'GAS-FWD-OCT-26', 'GAS_FORWARD', 'MMBtu'),
(33, 'COAL', 'USD', 'COAL-OPT-100', 'OPTION', 'MT'),
(34, 'POWER', 'EUR', 'PWR-SWAP-26', 'COMMODITY_SWAP', 'MWh'),
(35, 'GAS', 'USD', 'GAS-SWAP-26', 'COMMODITY_SWAP', 'MMBtu'),
(36, 'POWER', 'EUR', 'PWR-PPA-BIOMASS', 'RENEWABLE_PPA', 'MWh'),
(37, 'FREIGHT', 'USD', 'FRT-CAPE', 'FREIGHT', 'MT'),
(38, 'POWER', 'EUR', 'PWR-FWD-Q1-28', 'POWER_FORWARD', 'MWh'),
(39, 'GAS', 'USD', 'GAS-FWD-NOV-26', 'GAS_FORWARD', 'MMBtu'),
(40, 'OIL', 'USD', 'OIL-SWAP-27', 'COMMODITY_SWAP', 'bbl'),
(41, 'POWER', 'EUR', 'PWR-FWD-Q2-28', 'POWER_FORWARD', 'MWh'),
(42, 'GAS', 'USD', 'GAS-FWD-DEC-26', 'GAS_FORWARD', 'MMBtu'),
(43, 'COAL', 'USD', 'COAL-FWD-27', 'COMMODITY_FORWARD', 'MT'),
(44, 'POWER', 'EUR', 'PWR-OPT-CALL-55', 'OPTION', 'MWh'),
(45, 'GAS', 'USD', 'GAS-OPT-PUT-4', 'OPTION', 'MMBtu'),
(46, 'POWER', 'EUR', 'PWR-PPA-GEOTHERMAL', 'RENEWABLE_PPA', 'MWh'),
(47, 'FREIGHT', 'USD', 'FRT-HANDYSIZE', 'FREIGHT', 'MT'),
(48, 'POWER', 'EUR', 'PWR-FWD-Q3-28', 'POWER_FORWARD', 'MWh'),
(49, 'GAS', 'USD', 'GAS-FWD-JAN-27', 'GAS_FORWARD', 'MMBtu'),
(50, 'OIL', 'USD', 'OIL-OPT-65', 'OPTION', 'bbl');

-- ============================================================================
-- 2. CREDIT LIMITS (for counterparties)
-- ============================================================================
INSERT INTO ctrm.credit_limit (id, counterparty, limit_amount) VALUES
(1, 'ENEL_TRADING', 50000000.00),
(2, 'SHELL_TRADING', 75000000.00),
(3, 'BP_TRADING', 60000000.00),
(4, 'TOTAL_ENERGIES', 55000000.00),
(5, 'GAZPROM_EXPORT', 80000000.00),
(6, 'EDF_TRADING', 45000000.00),
(7, 'RWE_TRADING', 40000000.00),
(8, 'VATTENFALL', 35000000.00),
(9, 'STATKRAFT', 30000000.00),
(10, 'IBERDROLA', 25000000.00),
(11, 'ENGIE_TRADING', 65000000.00),
(12, 'EON_TRADING', 50000000.00),
(13, 'SSE_TRADING', 30000000.00),
(14, 'CEZ_TRADING', 20000000.00),
(15, 'PGE_TRADING', 15000000.00);

-- ============================================================================
-- 3. RISK LIMITS
-- ============================================================================
INSERT INTO risk_limit (limit_name, limit_type, limit_scope, scope_value, limit_value, warning_threshold, limit_unit, active, breach_action, created_by) VALUES
('Power Trading Desk - Position Limit', 'POSITION', 'PORTFOLIO', 'POWER_TRADING_DESK', 1000000.00, 800000.00, 'MWh', true, 'ALERT', 'SYSTEM'),
('Gas Trading Desk - Position Limit', 'POSITION', 'PORTFOLIO', 'GAS_TRADING_DESK', 5000000.00, 4000000.00, 'MMBtu', true, 'ALERT', 'SYSTEM'),
('Oil Trading Desk - Position Limit', 'POSITION', 'PORTFOLIO', 'OIL_TRADING_DESK', 100000.00, 80000.00, 'bbl', true, 'ALERT', 'SYSTEM'),
('Portfolio VaR Limit', 'VAR', 'PORTFOLIO', 'ALL', 1000000.00, 800000.00, 'USD', true, 'BLOCK', 'SYSTEM'),
('Delta Exposure Limit', 'DELTA', 'PORTFOLIO', 'ALL', 500000.00, 400000.00, 'USD', true, 'ALERT', 'SYSTEM'),
('Enel Concentration Limit', 'CONCENTRATION', 'COUNTERPARTY', 'ENEL_TRADING', 20000000.00, 15000000.00, 'USD', true, 'ESCALATE', 'SYSTEM'),
('Shell Concentration Limit', 'CONCENTRATION', 'COUNTERPARTY', 'SHELL_TRADING', 30000000.00, 25000000.00, 'USD', true, 'ESCALATE', 'SYSTEM'),
('Power Commodity Position', 'POSITION', 'COMMODITY', 'POWER', 2000000.00, 1500000.00, 'MWh', true, 'ALERT', 'SYSTEM'),
('Gas Commodity Position', 'POSITION', 'COMMODITY', 'GAS', 10000000.00, 8000000.00, 'MMBtu', true, 'ALERT', 'SYSTEM'),
('Oil Commodity Position', 'POSITION', 'COMMODITY', 'OIL', 200000.00, 150000.00, 'bbl', true, 'ALERT', 'SYSTEM');

-- ============================================================================
-- 4. TRADES (Single-leg trades for 6 months)
-- ============================================================================
INSERT INTO ctrm.trades (id, buy_sell, counterparty, created_at, pending_approval_role, portfolio, price, quantity, status, template_id, trade_id, updated_at, instrument_id, trade_date) VALUES
-- January 2026
(1, 'BUY', 'ENEL_TRADING', '2026-01-02 09:00:00', 'TRADER', 'POWER_TRADING_DESK', 65.50, 15000.00, 'APPROVED', 3, 'TRD-20260102-001', '2026-01-02 09:00:00', 1, '2026-01-02'),
(2, 'SELL', 'SHELL_TRADING', '2026-01-03 10:30:00', 'TRADER', 'GAS_TRADING_DESK', 5.25, 50000.00, 'APPROVED', 5, 'TRD-20260103-002', '2026-01-03 10:30:00', 5, '2026-01-03'),
(3, 'BUY', 'BP_TRADING', '2026-01-05 14:15:00', 'TRADER', 'OIL_TRADING_DESK', 58.75, 2500.00, 'PENDING_APPROVAL', 11, 'TRD-20260105-003', '2026-01-05 14:15:00', 11, '2026-01-05'),
(4, 'SELL', 'TOTAL_ENERGIES', '2026-01-07 11:45:00', 'TRADER', 'POWER_TRADING_DESK', 62.30, 8000.00, 'APPROVED', 2, 'TRD-20260107-004', '2026-01-07 11:45:00', 2, '2026-01-07'),
(5, 'BUY', 'GAZPROM_EXPORT', '2026-01-10 16:20:00', 'TRADER', 'GAS_TRADING_DESK', 5.80, 75000.00, 'APPROVED', 6, 'TRD-20260110-005', '2026-01-10 16:20:00', 6, '2026-01-10'),
-- February 2026
(6, 'SELL', 'EDF_TRADING', '2026-02-01 08:30:00', 'TRADER', 'POWER_TRADING_DESK', 68.90, 12000.00, 'APPROVED', 3, 'TRD-20260201-006', '2026-02-01 08:30:00', 1, '2026-02-01'),
(7, 'BUY', 'RWE_TRADING', '2026-02-05 13:10:00', 'TRADER', 'GAS_TRADING_DESK', 6.15, 40000.00, 'PENDING_APPROVAL', 7, 'TRD-20260205-007', '2026-02-05 13:10:00', 7, '2026-02-05'),
(8, 'SELL', 'VATTENFALL', '2026-02-08 15:45:00', 'TRADER', 'POWER_TRADING_DESK', 64.20, 18000.00, 'APPROVED', 4, 'TRD-20260208-008', '2026-02-08 15:45:00', 4, '2026-02-08'),
(9, 'BUY', 'STATKRAFT', '2026-02-12 10:00:00', 'TRADER', 'GAS_TRADING_DESK', 5.95, 60000.00, 'APPROVED', 8, 'TRD-20260212-009', '2026-02-12 10:00:00', 8, '2026-02-12'),
(10, 'SELL', 'IBERDROLA', '2026-02-15 14:30:00', 'TRADER', 'OIL_TRADING_DESK', 61.40, 1800.00, 'APPROVED', 11, 'TRD-20260215-010', '2026-02-15 14:30:00', 11, '2026-02-15'),
-- March 2026
(11, 'BUY', 'ENGIE_TRADING', '2026-03-01 09:15:00', 'TRADER', 'POWER_TRADING_DESK', 67.80, 22000.00, 'APPROVED', 3, 'TRD-20260301-011', '2026-03-01 09:15:00', 3, '2026-03-01'),
(12, 'SELL', 'EON_TRADING', '2026-03-05 11:20:00', 'TRADER', 'GAS_TRADING_DESK', 6.35, 55000.00, 'PENDING_APPROVAL', 9, 'TRD-20260305-012', '2026-03-05 11:20:00', 9, '2026-03-05'),
(13, 'BUY', 'SSE_TRADING', '2026-03-08 16:00:00', 'TRADER', 'POWER_TRADING_DESK', 69.50, 14000.00, 'APPROVED', 4, 'TRD-20260308-013', '2026-03-08 16:00:00', 4, '2026-03-08'),
(14, 'SELL', 'CEZ_TRADING', '2026-03-12 13:45:00', 'TRADER', 'GAS_TRADING_DESK', 6.05, 45000.00, 'APPROVED', 10, 'TRD-20260312-014', '2026-03-12 13:45:00', 10, '2026-03-12'),
(15, 'BUY', 'PGE_TRADING', '2026-03-15 10:30:00', 'TRADER', 'OIL_TRADING_DESK', 59.90, 3200.00, 'APPROVED', 11, 'TRD-20260315-015', '2026-03-15 10:30:00', 11, '2026-03-15'),
-- April 2026
(16, 'SELL', 'ENEL_TRADING', '2026-04-01 08:00:00', 'TRADER', 'POWER_TRADING_DESK', 71.20, 16000.00, 'APPROVED', 18, 'TRD-20260401-016', '2026-04-01 08:00:00', 18, '2026-04-01'),
(17, 'BUY', 'SHELL_TRADING', '2026-04-05 14:15:00', 'TRADER', 'GAS_TRADING_DESK', 6.55, 70000.00, 'PENDING_APPROVAL', 19, 'TRD-20260405-017', '2026-04-05 14:15:00', 19, '2026-04-05'),
(18, 'SELL', 'BP_TRADING', '2026-04-08 11:30:00', 'TRADER', 'POWER_TRADING_DESK', 73.80, 19000.00, 'APPROVED', 21, 'TRD-20260408-018', '2026-04-08 11:30:00', 21, '2026-04-08'),
(19, 'BUY', 'TOTAL_ENERGIES', '2026-04-12 15:45:00', 'TRADER', 'GAS_TRADING_DESK', 6.75, 65000.00, 'APPROVED', 22, 'TRD-20260412-019', '2026-04-12 15:45:00', 22, '2026-04-12'),
(20, 'SELL', 'GAZPROM_EXPORT', '2026-04-15 12:00:00', 'TRADER', 'OIL_TRADING_DESK', 63.25, 2800.00, 'APPROVED', 40, 'TRD-20260415-020', '2026-04-15 12:00:00', 40, '2026-04-15'),
-- May 2026
(21, 'BUY', 'EDF_TRADING', '2026-05-01 09:30:00', 'TRADER', 'POWER_TRADING_DESK', 75.40, 21000.00, 'APPROVED', 28, 'TRD-20260501-021', '2026-05-01 09:30:00', 28, '2026-05-01'),
(22, 'SELL', 'RWE_TRADING', '2026-05-05 13:20:00', 'TRADER', 'GAS_TRADING_DESK', 6.95, 58000.00, 'PENDING_APPROVAL', 29, 'TRD-20260505-022', '2026-05-05 13:20:00', 29, '2026-05-05'),
(23, 'BUY', 'VATTENFALL', '2026-05-08 16:10:00', 'TRADER', 'POWER_TRADING_DESK', 77.60, 17500.00, 'APPROVED', 31, 'TRD-20260508-023', '2026-05-08 16:10:00', 31, '2026-05-08'),
(24, 'SELL', 'STATKRAFT', '2026-05-12 10:45:00', 'TRADER', 'GAS_TRADING_DESK', 7.15, 72000.00, 'APPROVED', 32, 'TRD-20260512-024', '2026-05-12 10:45:00', 32, '2026-05-12'),
(25, 'BUY', 'IBERDROLA', '2026-05-15 14:25:00', 'TRADER', 'OIL_TRADING_DESK', 65.80, 2400.00, 'APPROVED', 40, 'TRD-20260515-025', '2026-05-15 14:25:00', 40, '2026-05-15'),
-- June 2026
(26, 'SELL', 'ENGIE_TRADING', '2026-06-01 08:45:00', 'TRADER', 'POWER_TRADING_DESK', 79.90, 23000.00, 'APPROVED', 38, 'TRD-20260601-026', '2026-06-01 08:45:00', 38, '2026-06-01'),
(27, 'BUY', 'EON_TRADING', '2026-06-05 11:35:00', 'TRADER', 'GAS_TRADING_DESK', 7.35, 68000.00, 'PENDING_APPROVAL', 39, 'TRD-20260605-027', '2026-06-05 11:35:00', 39, '2026-06-05'),
(28, 'SELL', 'SSE_TRADING', '2026-06-08 15:55:00', 'TRADER', 'POWER_TRADING_DESK', 82.10, 19500.00, 'APPROVED', 41, 'TRD-20260608-028', '2026-06-08 15:55:00', 41, '2026-06-08'),
(29, 'BUY', 'CEZ_TRADING', '2026-06-12 12:40:00', 'TRADER', 'GAS_TRADING_DESK', 7.55, 76000.00, 'APPROVED', 42, 'TRD-20260612-029', '2026-06-12 12:40:00', 42, '2026-06-12'),
(30, 'SELL', 'PGE_TRADING', '2026-06-15 13:15:00', 'TRADER', 'OIL_TRADING_DESK', 68.45, 3100.00, 'APPROVED', 40, 'TRD-20260615-030', '2026-06-15 13:15:00', 40, '2026-06-15');

-- ============================================================================
-- 5. MULTI-TRADE LEGS (for multi-leg trades)
-- ============================================================================
-- Assuming multi_trade_legs table exists with columns: id, trade_id, leg_number, instrument_id, quantity, price, buy_sell
INSERT INTO ctrm.multi_trade_legs (id, trade_id, leg_number, instrument_id, quantity, price, buy_sell) VALUES
-- Multi-trade 1: Power forward spread (Q1 vs Q2 2026)
(1, 'TRD-20260102-001', 1, 1, 15000.00, 65.50, 'BUY'),
(2, 'TRD-20260102-001', 2, 2, 15000.00, 62.30, 'SELL'),
-- Multi-trade 2: Gas calendar spread (Jan vs Feb 2026)
(3, 'TRD-20260103-002', 1, 5, 50000.00, 5.25, 'SELL'),
(4, 'TRD-20260103-002', 2, 6, 50000.00, 5.80, 'BUY'),
-- Multi-trade 3: Power option spread
(5, 'TRD-20260105-003', 1, 13, 2500.00, 2.50, 'BUY'),
(6, 'TRD-20260105-003', 2, 24, 2500.00, 1.80, 'SELL');

-- ============================================================================
-- 6. FORWARD CURVES
-- ============================================================================
-- Power forward curve for 2026
INSERT INTO ctrm.market_curve (id, curve_name, commodity, currency, pricing_date, source, status) VALUES
(1, 'POWER_EUR_2026', 'POWER', 'EUR', '2026-01-01', 'INTERNAL', 'ACTIVE'),
(2, 'GAS_USD_2026', 'GAS', 'USD', '2026-01-01', 'INTERNAL', 'ACTIVE'),
(3, 'OIL_USD_2026', 'OIL', 'USD', '2026-01-01', 'INTERNAL', 'ACTIVE');

-- Power curve points (monthly forward prices)
INSERT INTO ctrm.market_curve_point (id, curve_id, tenor, price, delivery_start, delivery_end) VALUES
(1, 1, '2026-01', 65.00, '2026-01-01', '2026-01-31'),
(2, 1, '2026-02', 66.50, '2026-02-01', '2026-02-28'),
(3, 1, '2026-03', 68.00, '2026-03-01', '2026-03-31'),
(4, 1, '2026-04', 69.50, '2026-04-01', '2026-04-30'),
(5, 1, '2026-05', 71.00, '2026-05-01', '2026-05-31'),
(6, 1, '2026-06', 72.50, '2026-06-01', '2026-06-30'),
(7, 1, '2026-07', 74.00, '2026-07-01', '2026-07-31'),
(8, 1, '2026-08', 75.50, '2026-08-01', '2026-08-31'),
(9, 1, '2026-09', 77.00, '2026-09-01', '2026-09-30'),
(10, 1, '2026-10', 78.50, '2026-10-01', '2026-10-31'),
(11, 1, '2026-11', 80.00, '2026-11-01', '2026-11-30'),
(12, 1, '2026-12', 81.50, '2026-12-01', '2026-12-31');

-- Gas curve points
INSERT INTO ctrm.market_curve_point (id, curve_id, tenor, price, delivery_start, delivery_end) VALUES
(13, 2, '2026-01', 5.20, '2026-01-01', '2026-01-31'),
(14, 2, '2026-02', 5.35, '2026-02-01', '2026-02-28'),
(15, 2, '2026-03', 5.50, '2026-03-01', '2026-03-31'),
(16, 2, '2026-04', 5.65, '2026-04-01', '2026-04-30'),
(17, 2, '2026-05', 5.80, '2026-05-01', '2026-05-31'),
(18, 2, '2026-06', 5.95, '2026-06-01', '2026-06-30'),
(19, 2, '2026-07', 6.10, '2026-07-01', '2026-07-31'),
(20, 2, '2026-08', 6.25, '2026-08-01', '2026-08-31'),
(21, 2, '2026-09', 6.40, '2026-09-01', '2026-09-30'),
(22, 2, '2026-10', 6.55, '2026-10-01', '2026-10-31'),
(23, 2, '2026-11', 6.70, '2026-11-01', '2026-11-30'),
(24, 2, '2026-12', 6.85, '2026-12-01', '2026-12-31');

-- Oil curve points
INSERT INTO ctrm.market_curve_point (id, curve_id, tenor, price, delivery_start, delivery_end) VALUES
(25, 3, '2026-01', 58.00, '2026-01-01', '2026-01-31'),
(26, 3, '2026-02', 59.50, '2026-02-01', '2026-02-28'),
(27, 3, '2026-03', 61.00, '2026-03-01', '2026-03-31'),
(28, 3, '2026-04', 62.50, '2026-04-01', '2026-04-30'),
(29, 3, '2026-05', 64.00, '2026-05-01', '2026-05-31'),
(30, 3, '2026-06', 65.50, '2026-06-01', '2026-06-30'),
(31, 3, '2026-07', 67.00, '2026-07-01', '2026-07-31'),
(32, 3, '2026-08', 68.50, '2026-08-01', '2026-08-31'),
(33, 3, '2026-09', 70.00, '2026-09-01', '2026-09-30'),
(34, 3, '2026-10', 71.50, '2026-10-01', '2026-10-31'),
(35, 3, '2026-11', 73.00, '2026-11-01', '2026-11-30'),
(36, 3, '2026-12', 74.50, '2026-12-01', '2026-12-31');

-- ============================================================================
-- 7. POSITIONS (Aggregated from trades)
-- ============================================================================
INSERT INTO position (position_date, portfolio, commodity, delivery_start, delivery_end, long_quantity, short_quantity, net_quantity, long_mtm, short_mtm, net_mtm, delta, gamma, vega, trade_count) VALUES
-- January 2026 positions
('2026-01-31', 'POWER_TRADING_DESK', 'POWER', '2026-01-01', '2026-03-31', 15000.00, 8000.00, 7000.00, 982500.00, 496240.00, 486260.00, 7000.00, 0.00, 0.00, 2),
('2026-01-31', 'GAS_TRADING_DESK', 'GAS', '2026-01-01', '2026-01-31', 0.00, 50000.00, -50000.00, 0.00, 262500.00, -262500.00, -50000.00, 0.00, 0.00, 1),
('2026-01-31', 'GAS_TRADING_DESK', 'GAS', '2026-02-01', '2026-02-28', 75000.00, 0.00, 75000.00, 435000.00, 0.00, 435000.00, 75000.00, 0.00, 0.00, 1),
-- February 2026 positions
('2026-02-28', 'POWER_TRADING_DESK', 'POWER', '2026-01-01', '2026-03-31', 15000.00, 20000.00, -5000.00, 982500.00, 1242400.00, -260900.00, -5000.00, 0.00, 0.00, 3),
('2026-02-28', 'GAS_TRADING_DESK', 'GAS', '2026-02-01', '2026-02-28', 40000.00, 0.00, 40000.00, 234000.00, 0.00, 234000.00, 40000.00, 0.00, 0.00, 1),
('2026-02-28', 'GAS_TRADING_DESK', 'GAS', '2026-03-01', '2026-03-31', 0.00, 60000.00, -60000.00, 0.00, 357000.00, -357000.00, -60000.00, 0.00, 0.00, 1),
-- March 2026 positions
('2026-03-31', 'POWER_TRADING_DESK', 'POWER', '2026-01-01', '2026-03-31', 37000.00, 20000.00, 17000.00, 2409500.00, 1242400.00, 1167100.00, 17000.00, 0.00, 0.00, 4),
('2026-03-31', 'GAS_TRADING_DESK', 'GAS', '2026-03-01', '2026-03-31', 0.00, 100000.00, -100000.00, 0.00, 605000.00, -605000.00, -100000.00, 0.00, 0.00, 2),
-- April-June 2026 positions (aggregated)
('2026-04-30', 'POWER_TRADING_DESK', 'POWER', '2026-01-01', '2026-12-31', 89000.00, 54500.00, 34500.00, 6500000.00, 3800000.00, 2700000.00, 34500.00, 0.00, 0.00, 8),
('2026-04-30', 'GAS_TRADING_DESK', 'GAS', '2026-01-01', '2026-12-31', 251000.00, 0.00, 251000.00, 1500000.00, 0.00, 1500000.00, 251000.00, 0.00, 0.00, 6),
('2026-04-30', 'OIL_TRADING_DESK', 'OIL', '2026-01-01', '2026-12-31', 0.00, 7500.00, -7500.00, 0.00, 450000.00, -450000.00, -7500.00, 0.00, 0.00, 3),
('2026-05-31', 'POWER_TRADING_DESK', 'POWER', '2026-01-01', '2026-12-31', 110000.00, 54500.00, 55500.00, 8000000.00, 3800000.00, 4200000.00, 55500.00, 0.00, 0.00, 10),
('2026-05-31', 'GAS_TRADING_DESK', 'GAS', '2026-01-01', '2026-12-31', 251000.00, 58000.00, 193000.00, 1500000.00, 400000.00, 1100000.00, 193000.00, 0.00, 0.00, 7),
('2026-06-30', 'POWER_TRADING_DESK', 'POWER', '2026-01-01', '2026-12-31', 130500.00, 74000.00, 56500.00, 9500000.00, 5200000.00, 4300000.00, 56500.00, 0.00, 0.00, 12),
('2026-06-30', 'GAS_TRADING_DESK', 'GAS', '2026-01-01', '2026-12-31', 251000.00, 136000.00, 115000.00, 1500000.00, 950000.00, 550000.00, 115000.00, 0.00, 0.00, 8),
('2026-06-30', 'OIL_TRADING_DESK', 'OIL', '2026-01-01', '2026-12-31', 0.00, 10600.00, -10600.00, 0.00, 680000.00, -680000.00, -10600.00, 0.00, 0.00, 4);

-- ============================================================================
-- END OF SEED DATA
-- ============================================================================
