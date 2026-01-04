-- Sample commodity data for CTRM system
-- These commodities are referenced by instruments and templates

INSERT INTO ctrm.commodity (name) VALUES
('POWER'),
('GAS'),
('OIL'),
('COAL'),
('FREIGHT')
ON CONFLICT (name) DO NOTHING;