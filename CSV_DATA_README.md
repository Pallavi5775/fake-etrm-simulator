# CTRM Simulator - Sample Data CSVs

This directory contains sample CSV files with realistic data for testing and demonstrating the Commodity Trading and Risk Management (CTRM) simulator.

## Files Overview

### Reference Data
- **`commodities.csv`** - Master list of commodities (POWER, GAS, COAL, OIL, CARBON_CREDITS, RENEWABLE_CERTIFICATES)
- **`counterparties.csv`** - Trading counterparties with credit ratings and countries
- **`portfolios.csv`** - Trading portfolios with risk owners
- **`instruments.csv`** - Financial instruments with commodity associations and specifications
- **`roles.csv`** - System roles for user access control and permissions

### Trading Templates
- **`deal_templates.csv`** - Pre-configured deal templates for different instrument types

### Market Data
- **`forward_curves.csv`** - Forward price curves for future delivery periods
- **`yield_curves.csv`** - Discount rate curves for different currencies
- **`price_curves.csv`** - Historical and spot price curves
- **`volatility.csv`** - Volatility data for options pricing
- **`market_prices.csv`** - Current spot market prices

### Specialized Data
- **`weather_data.csv`** - Weather conditions for renewable energy pricing
- **`generation_forecast.csv`** - Power generation forecasts for renewable assets

## Data Characteristics

- **Currencies**: EUR (European), USD (American), GBP (British)
- **Units**: MWh (energy), MT (metric tons), BBL (barrels), TON (tons)
- **Time Period**: January 2024 - December 2025
- **Geographic Coverage**: Europe (Germany, Spain, France, UK, Nordic countries)

## Usage

These CSV files can be used to:
1. Populate the database for testing
2. Validate pricing engines
3. Demonstrate risk calculations
4. Test trading workflows
5. Generate sample reports

## Data Quality

- Realistic market prices based on 2024 energy markets
- Proper credit ratings (AAA, AA, A+)
- Geographically accurate weather patterns
- Industry-standard instrument specifications
- Time-series data for trend analysis