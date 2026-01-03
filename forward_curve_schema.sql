-- Forward Curve Schema
CREATE TABLE IF NOT EXISTS ctrm.forward_curve (
    id BIGSERIAL PRIMARY KEY,
    instrument_id BIGINT NOT NULL,
    delivery_date DATE NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    curve_date DATE NOT NULL DEFAULT CURRENT_DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (instrument_id) REFERENCES ctrm.instruments(id),
    UNIQUE(instrument_id, delivery_date, curve_date)
);

-- Sample Forward Curve Data for Power Forward (instrument_id = 4, PWR-Q1-25)
-- Trade price was 55.00, let's add market prices around that

INSERT INTO ctrm.forward_curve (instrument_id, delivery_date, price, curve_date) VALUES
-- Today's curve (2026-01-03)
(4, '2026-01-02', 60.50, '2026-01-03'),  -- Market moved up from 55 to 60.50
(4, '2026-01-03', 60.75, '2026-01-03'),
(4, '2026-01-04', 60.25, '2026-01-03'),
(4, '2026-01-05', 59.80, '2026-01-03'),
(4, '2026-01-06', 59.50, '2026-01-03'),

-- Yesterday's curve (2026-01-02) - for P&L calculation
(4, '2026-01-02', 58.00, '2026-01-02'),
(4, '2026-01-03', 58.25, '2026-01-02'),
(4, '2026-01-04', 57.80, '2026-01-02'),
(4, '2026-01-05', 57.50, '2026-01-02'),

-- Add curves for other instruments if needed
-- Gas Forward (instrument_id = 3)
(3, '2026-01-02', 3.50, '2026-01-03'),
(3, '2026-01-03', 3.55, '2026-01-03'),
(3, '2026-01-04', 3.52, '2026-01-03'),

-- Future dates for testing
(4, '2026-01-10', 59.00, '2026-01-03'),
(4, '2026-01-15', 58.50, '2026-01-03'),
(4, '2026-01-20', 58.00, '2026-01-03')
ON CONFLICT (instrument_id, delivery_date, curve_date) DO NOTHING;
