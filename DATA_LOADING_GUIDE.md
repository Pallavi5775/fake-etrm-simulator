# CTRM Data Loading Script

This script provides examples of how to load the CSV data into the CTRM simulator database.

## Prerequisites

- PostgreSQL database running
- CTRM simulator application configured
- CSV files in the project root directory

## Loading Order

Load data in this order to respect foreign key constraints:

1. **Reference Data**
   ```sql
   -- Commodities (no dependencies)
   \COPY commodities(name) FROM 'commodities.csv' WITH CSV HEADER;

   -- Counterparties (no dependencies)
   \COPY counterparties(name, credit_rating, country, active) FROM 'counterparties.csv' WITH CSV HEADER;

   -- Portfolios (no dependencies)
   \COPY portfolios(name, description, risk_owner, active) FROM 'portfolios.csv' WITH CSV HEADER;

   -- Roles (no dependencies)
   \COPY ctrm.roles(role_name, display_name, description, active) FROM 'roles.csv' WITH CSV HEADER;

   -- Users (depends on roles)
   \COPY ctrm.users(username, password_hash, email, full_name, role, active) FROM 'users.csv' WITH CSV HEADER;
   ```

2. **Instruments** (depends on commodities)
   ```sql
   -- First, create a temporary table to handle the commodity reference
   CREATE TEMP TABLE temp_instruments (
       instrument_code TEXT,
       commodity_name TEXT,
       currency TEXT,
       unit TEXT,
       instrument_type TEXT
   );

   \COPY temp_instruments FROM 'instruments.csv' WITH CSV HEADER;

   -- Insert instruments with commodity_id lookup
   INSERT INTO instruments (instrument_code, commodity_id, currency, unit, instrument_type)
   SELECT ti.instrument_code, c.id, ti.currency, ti.unit, ti.instrument_type::instrument_type
   FROM temp_instruments ti
   JOIN commodities c ON c.name = ti.commodity_name;
   ```

3. **Deal Templates** (depends on commodities and instruments)
   ```sql
   CREATE TEMP TABLE temp_deal_templates (
       template_name TEXT,
       commodity_name TEXT,
       instrument_type TEXT,
       instrument_code TEXT,
       pricing_model TEXT,
       auto_approval_allowed BOOLEAN,
       default_quantity DECIMAL,
       default_price DECIMAL,
       unit TEXT,
       currency TEXT,
       mtm_approval_threshold DECIMAL
   );

   \COPY temp_deal_templates FROM 'deal_templates.csv' WITH CSV HEADER;

   INSERT INTO deal_templates (template_name, commodity_id, instrument_type, instrument_id, pricing_model, auto_approval_allowed, default_quantity, default_price, unit, currency, mtm_approval_threshold)
   SELECT tdt.template_name, c.id, tdt.instrument_type, i.id, tdt.pricing_model, tdt.auto_approval_allowed, tdt.default_quantity, tdt.default_price, tdt.unit, tdt.currency, tdt.mtm_approval_threshold
   FROM temp_deal_templates tdt
   JOIN commodities c ON c.name = tdt.commodity_name
   JOIN instruments i ON i.instrument_code = tdt.instrument_code;
   ```

4. **Market Data** (depends on instruments)
   ```sql
   -- Forward Curves
   CREATE TEMP TABLE temp_forward_curves (
       instrument_code TEXT,
       delivery_date DATE,
       price DECIMAL,
       curve_date DATE
   );

   \COPY temp_forward_curves FROM 'forward_curves.csv' WITH CSV HEADER;

   INSERT INTO forward_curve (instrument_id, delivery_date, price, curve_date)
   SELECT i.id, tfc.delivery_date, tfc.price, tfc.curve_date
   FROM temp_forward_curves tfc
   JOIN instruments i ON i.instrument_code = tfc.instrument_code;

   -- Yield Curves (no dependencies)
   \COPY yield_curve(curve_name, date, yield) FROM 'yield_curves.csv' WITH CSV HEADER;

   -- Price Curves (no dependencies)
   \COPY price_curve(curve_name, date, price) FROM 'price_curves.csv' WITH CSV HEADER;

   -- Volatility (depends on instruments)
   CREATE TEMP TABLE temp_volatility (
       instrument_code TEXT,
       date DATE,
       value DECIMAL
   );

   \COPY temp_volatility FROM 'volatility.csv' WITH CSV HEADER;

   INSERT INTO volatility (instrument_id, date, value)
   SELECT i.id, tv.date, tv.value
   FROM temp_volatility tv
   JOIN instruments i ON i.instrument_code = tv.instrument_code;

   -- Market Prices (depends on instruments)
   CREATE TEMP TABLE temp_market_prices (
       instrument_code TEXT,
       price DECIMAL
   );

   \COPY temp_market_prices FROM 'market_prices.csv' WITH CSV HEADER;

   INSERT INTO market_price (instrument_code, price)
   SELECT tmp.instrument_code, tmp.price
   FROM temp_market_prices tmp;
   ```

5. **Specialized Data**
   ```sql
   -- Weather Data (no dependencies)
   \COPY weather_data(location, date, temperature, precipitation) FROM 'weather_data.csv' WITH CSV HEADER;

   -- Generation Forecast (no dependencies)
   \COPY generation_forecast(plant_name, date, forecast_mwh) FROM 'generation_forecast.csv' WITH CSV HEADER;
   ```

## Alternative: Spring Batch Loading

For a more robust solution, consider implementing Spring Batch jobs to load this data with proper validation and error handling.

## Validation

After loading, verify data integrity:

```sql
-- Check record counts
SELECT 'commodities' as table_name, COUNT(*) as count FROM commodities
UNION ALL
SELECT 'counterparties', COUNT(*) FROM counterparties
UNION ALL
SELECT 'portfolios', COUNT(*) FROM portfolios
UNION ALL
SELECT 'roles', COUNT(*) FROM ctrm.roles
UNION ALL
SELECT 'instruments', COUNT(*) FROM instruments
UNION ALL
SELECT 'forward_curve', COUNT(*) FROM forward_curve;
```

## Notes

- Adjust file paths as needed for your environment
- Ensure proper permissions for database operations
- Consider adding transaction wrapping for production loads
- Validate data types match database schema