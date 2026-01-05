-- CTRM System Roles
-- Insert statements for the roles table

INSERT INTO ctrm.roles (role_name, display_name, description, active) VALUES
('ADMIN', 'Administrator', 'Full system access with all permissions including user management, system configuration, and audit oversight', true);

INSERT INTO ctrm.roles (role_name, display_name, description, active) VALUES
('RISK', 'Risk Manager', 'Monitor and manage risk exposures, set risk limits, and generate risk reports', true);

INSERT INTO ctrm.roles (role_name, display_name, description, active) VALUES
('SENIOR_TRADER', 'Senior Trader', 'Execute complex trades, manage large positions, and lead trading strategies', true);

INSERT INTO ctrm.roles (role_name, display_name, description, active) VALUES
('HEAD_TRADER', 'Head Trader', 'Oversee all trading activities, manage trader teams, and set trading policies', true);

INSERT INTO ctrm.roles (role_name, display_name, description, active) VALUES
('COMPLIANCE', 'Compliance Officer', 'Ensure regulatory compliance, review trades, and manage compliance reporting', true);

INSERT INTO ctrm.roles (role_name, display_name, description, active) VALUES
('CFO', 'CFO', 'Chief Financial Officer with access to financial reports, treasury management, and executive oversight', true);

INSERT INTO ctrm.roles (role_name, display_name, description, active) VALUES
('CARBON_TRADER', 'Carbon Trader', 'Specialized trading role for carbon credits and emissions trading', true);