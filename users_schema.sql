-- Users table for authentication
CREATE TABLE ctrm.users (
    user_id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    full_name VARCHAR(255),
    role VARCHAR(50) NOT NULL,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create index on username for faster lookups
CREATE INDEX idx_users_username ON ctrm.users(username);
CREATE INDEX idx_users_email ON ctrm.users(email);

-- Demo users (password: password123 for all)
-- Password hash is SHA-256 of "password123"
INSERT INTO ctrm.users (username, password_hash, email, full_name, role, active)
VALUES 
    ('risk_user', 'EF92B778BAFE771E89245B89ECBC08A44A4E166C06659911881F383D4473E94F', 'risk@example.com', 'Risk Manager', 'RISK', true),
    ('trader1', 'EF92B778BAFE771E89245B89ECBC08A44A4E166C06659911881F383D4473E94F', 'trader1@example.com', 'John Trader', 'SENIOR_TRADER', true),
    ('trader2', 'EF92B778BAFE771E89245B89ECBC08A44A4E166C06659911881F383D4473E94F', 'trader2@example.com', 'Sarah Head', 'HEAD_TRADER', true),
    ('compliance', 'EF92B778BAFE771E89245B89ECBC08A44A4E166C06659911881F383D4473E94F', 'compliance@example.com', 'Compliance Officer', 'COMPLIANCE', true),
    ('cfo', 'EF92B778BAFE771E89245B89ECBC08A44A4E166C06659911881F383D4473E94F', 'cfo@example.com', 'Chief Financial Officer', 'CFO', true),
    ('admin', 'EF92B778BAFE771E89245B89ECBC08A44A4E166C06659911881F383D4473E94F', 'admin@example.com', 'System Admin', 'ADMIN', true);
