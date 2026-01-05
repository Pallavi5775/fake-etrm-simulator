-- CTRM System Users
-- Insert statements for the users table

INSERT INTO ctrm.users (username, password_hash, email, full_name, role, active) VALUES
('admin', 'admin123', 'admin@ctrm-simulator.com', 'System Administrator', 'ADMIN', true);

INSERT INTO ctrm.users (username, password_hash, email, full_name, role, active) VALUES
('risk_mgr', 'risk123', 'risk@ctrm-simulator.com', 'John Risk', 'RISK', true);

INSERT INTO ctrm.users (username, password_hash, email, full_name, role, active) VALUES
('senior_trader', 'trader123', 'senior@ctrm-simulator.com', 'Sarah Trader', 'SENIOR_TRADER', true);

INSERT INTO ctrm.users (username, password_hash, email, full_name, role, active) VALUES
('head_trader', 'head123', 'head@ctrm-simulator.com', 'Michael Head', 'HEAD_TRADER', true);

INSERT INTO ctrm.users (username, password_hash, email, full_name, role, active) VALUES
('compliance', 'comp123', 'compliance@ctrm-simulator.com', 'Lisa Compliance', 'COMPLIANCE', true);

INSERT INTO ctrm.users (username, password_hash, email, full_name, role, active) VALUES
('cfo', 'cfo123', 'cfo@ctrm-simulator.com', 'David CFO', 'CFO', true);