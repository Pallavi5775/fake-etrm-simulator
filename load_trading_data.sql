-- Load Essential Reference Data for Trading
-- Load portfolios and counterparties from CSV files

-- Set schema
SET search_path TO ctrm;

-- Load counterparties
\COPY counterparties(name, credit_rating, country, active) FROM 'counterparties.csv' WITH CSV HEADER;

-- Load portfolios
\COPY portfolios(name, description, risk_owner, active) FROM 'portfolios.csv' WITH CSV HEADER;

COMMIT;