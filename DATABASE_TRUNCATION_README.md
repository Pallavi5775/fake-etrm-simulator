# Database Truncation and Setup Guide

This guide provides multiple ways to truncate (clear) all data from the CTRA Commodity Trading and Risk Management simulator database, and set up initial data with IDs starting from 1.

## ⚠️ WARNING
**Truncating the database will permanently delete ALL data!** This operation cannot be undone. Make sure you have backups if needed.

## Methods Available

### 1. SQL Script (Recommended for Development)

Run the provided SQL script directly against your PostgreSQL database:

```bash
# Using psql command line
psql -h localhost -p 5432 -U ctrm_user -d ctrm_db -f truncate_database.sql

# Or use the batch file (Windows)
truncate_db.bat
```

**Parameters for batch file:**
- `truncate_db.bat [host] [port] [database] [user] [password]`
- Default: `localhost 5432 ctrm_db ctrm_user ctrm_pass`

### 2. Complete Setup Script (Truncate + Insert Commodities & Instruments)

For a fresh start with commodities and instruments data, use the setup script:

```bash
# Using psql command line
psql -h localhost -p 5432 -U ctrm_user -d ctrm_db -f setup_instruments.sql

# Or use the batch file (Windows)
setup_instruments.bat
```

**What this script does:**
- Truncates all tables (same as truncate_database.sql)
- Resets all sequences to start from 1
- Inserts 8 commodities (WTI Crude Oil, Brent Crude Oil, Natural Gas, Power Baseload, Gold, Copper, Corn, Soybeans)
- Inserts 20 option instruments across Energy, Metals, and Agriculture categories

**Parameters for batch file:**
- `setup_instruments.bat [host] [port] [database] [user] [password]`
- Default: `localhost 5432 ctrm_db ctrm_user ctrm_pass`

### 3. REST API Endpoint (For Running Application)

When the application is running, you can truncate the database via REST API:

```bash
# Truncate all data
curl -X POST "http://localhost:8080/api/options/admin/truncate-database?confirm=YES"
```

**Response:**
- Success: `"Database truncated successfully. All data has been deleted."`
- Error: `"Confirmation required. Add ?confirm=YES to proceed."` (if confirm parameter missing)

### 4. Programmatic Access (Java Code)

Use the `DatabaseAdminService` in your Java code:

```java
@Autowired
private DatabaseAdminService databaseAdminService;

public void clearDatabase() {
    databaseAdminService.truncateAllTables();
}
```

## What Gets Truncated

The truncation script clears data from the following tables in the correct order:

1. **Pricing & Valuation**: valuation_result, valuation_run, scenario_result, pnl_explain
2. **Options Data**: option_forward_curves, option_volatility, option_yield_curves
3. **Market Data**: market_prices, price_curves, yield_curves, volatility_surfaces, forecast_prices, weather_data, generation_forecast
4. **Trades**: trade_legs, trades, trade_versions, trade_events
5. **Instruments**: All instrument types and base instruments table
6. **Reference Data**: commodities, counterparties, portfolios
7. **Users & Security**: user_roles, users, roles
8. **Risk & Credit**: credit_limits

## What Gets Set Up (setup_instruments.sql)

The complete setup script inserts the following data after truncation:

**Commodities (8 total):**
- WTI Crude Oil, Brent Crude Oil, Natural Gas, Power Baseload
- Gold, Copper, Corn, Soybeans

**Instruments (20 total - all OPTION type):**
- **Energy - Oil**: WTI & Brent Futures Calls/Puts for 2026
- **Energy - Gas**: Natural Gas Futures Calls/Puts Q1 2026
- **Energy - Power**: Power Baseload Forward Calls/Puts 2026
- **Metals**: Gold & Copper Futures Calls/Puts 2026
- **Agriculture**: Corn & Soybean Futures Calls/Puts 2026

All IDs start from 1 due to sequence resets.

## Foreign Key Handling

The script uses PostgreSQL's `CASCADE` option and temporarily disables foreign key constraints to ensure clean truncation without constraint violations.

## Sequence Reset (Optional)

If you want to reset auto-increment sequences back to 1, uncomment the ALTER SEQUENCE statements in the SQL script.

## Best Practices

1. **Always backup** your data before truncation
2. **Test in development** before using in production
3. **Use the confirmation parameter** when using the REST API
4. **Run during maintenance windows** to avoid disrupting users

## Troubleshooting

- **Permission denied**: Ensure your database user has TRUNCATE permissions
- **Foreign key violations**: The CASCADE option should handle this, but check your PostgreSQL version
- **Connection issues**: Verify your database connection parameters