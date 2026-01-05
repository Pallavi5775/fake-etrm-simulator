-- Load Reference Data for CTRM Simulator
-- Load portfolios and counterparties from CSV files

-- Set schema
SET search_path TO ctrm;

-- Load counterparties
\COPY counterparties(name, credit_rating, country, active) FROM 'counterparties.csv' WITH CSV HEADER;

-- Load portfolios
\COPY portfolios(name, description, risk_owner, active) FROM 'portfolios.csv' WITH CSV HEADER;

-- Load roles
\COPY roles(role_name, display_name, description, active) FROM 'roles.csv' WITH CSV HEADER;

-- Load users (depends on roles)
\COPY users(username, password_hash, email, full_name, role, active) FROM 'users.csv' WITH CSV HEADER;

COMMIT;