-- Roles table for storing system roles
CREATE TABLE IF NOT EXISTS ctrm.roles (
    role_id BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    display_name VARCHAR(100) NOT NULL,
    description TEXT,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index for faster lookups
CREATE INDEX idx_roles_role_name ON ctrm.roles(role_name);
CREATE INDEX idx_roles_active ON ctrm.roles(active);

-- Insert default system roles
INSERT INTO ctrm.roles (role_name, display_name, description) VALUES
('RISK', 'Risk Manager', 'Risk management and oversight. Can approve trades, configure risk limits.'),
('SENIOR_TRADER', 'Senior Trader', 'Senior trading desk member. Can book trades and approve junior trades.'),
('HEAD_TRADER', 'Head Trader', 'Head of trading desk. Can book trades and approve high-value trades.'),
('COMPLIANCE', 'Compliance Officer', 'Compliance and regulatory oversight. Can approve high-risk counterparty trades.'),
('CFO', 'Chief Financial Officer', 'Financial oversight. Required for very high-value trades.'),
('ADMIN', 'Administrator', 'System administrator with full access to all features.');

-- Role permissions mapping (optional - for future enhancement)
CREATE TABLE IF NOT EXISTS ctrm.role_permissions (
    permission_id BIGSERIAL PRIMARY KEY,
    role_id BIGINT NOT NULL,
    permission_name VARCHAR(100) NOT NULL,
    resource VARCHAR(100) NOT NULL,
    action VARCHAR(50) NOT NULL,
    FOREIGN KEY (role_id) REFERENCES ctrm.roles(role_id) ON DELETE CASCADE,
    UNIQUE(role_id, permission_name)
);

-- Sample permissions for RISK role
INSERT INTO ctrm.role_permissions (role_id, permission_name, resource, action) VALUES
((SELECT role_id FROM ctrm.roles WHERE role_name = 'RISK'), 'APPROVE_TRADES', 'trades', 'approve'),
((SELECT role_id FROM ctrm.roles WHERE role_name = 'RISK'), 'VIEW_POSITIONS', 'positions', 'read'),
((SELECT role_id FROM ctrm.roles WHERE role_name = 'RISK'), 'CONFIGURE_LIMITS', 'risk_limits', 'write'),
((SELECT role_id FROM ctrm.roles WHERE role_name = 'RISK'), 'CONFIGURE_RULES', 'approval_rules', 'write');

-- Sample permissions for SENIOR_TRADER role
INSERT INTO ctrm.role_permissions (role_id, permission_name, resource, action) VALUES
((SELECT role_id FROM ctrm.roles WHERE role_name = 'SENIOR_TRADER'), 'BOOK_TRADES', 'trades', 'create'),
((SELECT role_id FROM ctrm.roles WHERE role_name = 'SENIOR_TRADER'), 'VIEW_TRADES', 'trades', 'read'),
((SELECT role_id FROM ctrm.roles WHERE role_name = 'SENIOR_TRADER'), 'APPROVE_TRADES', 'trades', 'approve'),
((SELECT role_id FROM ctrm.roles WHERE role_name = 'SENIOR_TRADER'), 'MANAGE_TEMPLATES', 'templates', 'write');

-- Sample permissions for ADMIN role
INSERT INTO ctrm.role_permissions (role_id, permission_name, resource, action) VALUES
((SELECT role_id FROM ctrm.roles WHERE role_name = 'ADMIN'), 'FULL_ACCESS', '*', '*');
