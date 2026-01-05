# CTRM Options Market Data Templates

This directory contains templates and tools for populating options market data in your CTRM system.

## 📊 Data Structure

### Futures vs Forwards Options

**Futures Options** (Exchange-traded):
- Use **Black pricing model** (no discounting)
- Examples: WTI futures options, Gold futures options
- `underlyingType: "FUTURES"`

**Forward Options** (OTC):
- Use **Black-76 pricing model** (with discounting)
- Examples: Power forward options, custom commodity forwards
- `underlyingType: "FORWARD"` or `null`

## 🚀 Quick Start

### Option 1: REST API (Recommended)

1. **Load Sample Data:**
   ```bash
   curl -X POST http://localhost:8080/api/options/load-sample-data
   ```

2. **Check Data Summary:**
   ```bash
   curl http://localhost:8080/api/options/data-summary
   ```

### Option 2: SQL Script

Run the provided SQL script:
```bash
psql -d your_database -f options_market_data_template.sql
```

### Option 3: Programmatic Loading

Use the `OptionsMarketDataLoader` in your application startup or tests.

## 📈 Sample Data Included

### Energy Commodities
- **Oil**: WTI & Brent futures options
- **Gas**: Natural gas futures options
- **Power**: Baseload forward options

### Metals
- **Gold**: Futures options
- **Copper**: Futures options

### Agriculture
- **Corn**: Futures options
- **Soybeans**: Futures options

## 🎯 Market Data Tables

| Table | Purpose | Sample Count |
|-------|---------|--------------|
| `option_forward_curves` | Forward prices for options | 14 records |
| `option_volatility` | Volatility surfaces | 14 records |
| `option_yield_curves` | Discount curves | 9 records |

## 📝 Creating Custom Options

### Futures Option Example:
```java
CommodityOptionInstrument option = new CommodityOptionInstrument();
option.setInstrumentCode("WTI_FUTURES_CALL_2026");
option.setStrikePrice(BigDecimal.valueOf(80.00));
option.setExpiryDate(LocalDate.of(2026, 12, 31));
option.setOptionType("CALL");
option.setUnderlyingType("FUTURES");  // Key: Uses Black model
option.setCurrency("USD");
```

### Forward Option Example:
```java
CommodityOptionInstrument option = new CommodityOptionInstrument();
option.setInstrumentCode("POWER_BASELOAD_FORWARD_PUT_2026");
option.setStrikePrice(BigDecimal.valueOf(50.00));
option.setExpiryDate(LocalDate.of(2026, 12, 31));
option.setOptionType("PUT");
option.setUnderlyingType("FORWARD");  // Key: Uses Black-76 model
option.setCurrency("USD");
```

## ⚙️ Configuration

The system automatically routes to the correct pricing engine based on `underlyingType`:

- `"FUTURES"` → `BlackPricingEngine`
- `"FORWARD"` or `null` → `Black76PricingEngine`

## 🔍 Verification

After loading data, verify with:

```sql
-- Check all option forward curves
SELECT instrument_code, forward_price FROM option_forward_curves;

-- Check volatility data
SELECT instrument_code, value as volatility FROM option_volatility;

-- Check yield curves
SELECT curve_name, yield FROM option_yield_curves;
```

## 📊 Realistic Values

The sample data uses realistic market values:
- **Oil prices**: $75-80/barrel
- **Gas prices**: $3-4/MMBtu
- **Power prices**: $45-50/MWh
- **Volatilities**: 15-35% (higher for energy, lower for power)
- **Discount rates**: 3.75-4.50% (currency-based)

## 🚨 Important Notes

1. **Dates**: All data uses 2026-01-05 as the valuation date
2. **Currency**: USD is primary, with EUR/GBP examples
3. **Maturities**: Mix of quarterly and yearly contracts
4. **Volatilities**: Calibrated to realistic market levels

## 🧪 Testing

Use the sample data to test:
- Option pricing for different underlying types
- Greeks calculation (delta, gamma, vega, theta)
- Risk metrics and P&L attribution
- Scenario analysis and stress testing