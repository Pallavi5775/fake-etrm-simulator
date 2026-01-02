# Phase 2 Implementation - Advanced Pricing & Risk

**Completion Date:** January 2, 2026  
**Status:** ✅ COMPLETE

---

## Overview

Phase 2 extends the Endur-style pricing infrastructure with advanced features for portfolio management, P&L attribution, and risk analysis. This phase enables enterprise-grade valuation workflows including batch processing, daily P&L calculation, and stress testing.

---

## 🎯 Phase 2 Deliverables

### 1. Volatility Surface Management
- **Entities:** `VolatilitySurface`, `VolatilityPoint`
- **Purpose:** Store and retrieve implied volatility data for options pricing
- **Features:**
  - Support for multiple surface types (IMPLIED, HISTORICAL)
  - Strike x expiry grid storage
  - Time-series surface management

### 2. Batch Valuation Service
- **Service:** `BatchValuationService`
- **Purpose:** Revalue entire portfolios in parallel
- **Features:**
  - Multi-threaded valuation engine (10 thread pool)
  - Portfolio filtering
  - Run tracking with success/failure counts
  - Endur-style batch processing

### 3. P&L Attribution
- **Service:** `PnlAttributionService`
- **Entity:** `PnlExplain`
- **Purpose:** Daily P&L calculation with risk factor attribution
- **Attribution Components:**
  - Spot price movement (delta effect)
  - Forward curve movement
  - Volatility movement (vega effect)
  - Time decay (theta)
  - Carry (interest/dividend)
  - Unexplained P&L tracking

### 4. Scenario Analysis Framework
- **Service:** `ScenarioService`
- **Entities:** `ValuationScenario`, `ScenarioResult`
- **Purpose:** Stress testing and what-if analysis
- **Scenario Types:**
  - `SPOT_SHOCK` - Spot price shocks (±X%)
  - `CURVE_SHIFT` - Parallel curve shifts
  - `VOL_SHOCK` - Volatility shocks
  - `HISTORICAL` - Historical scenario replay

### 5. REST API for Portfolio Operations
- **Controller:** `BatchValuationController`
- **Endpoints:**
  - `POST /api/valuation/batch` - Run batch valuation
  - `GET /api/valuation/batch/runs` - Get valuation runs
  - `POST /api/valuation/pnl/calculate` - Calculate daily P&L
  - `GET /api/valuation/pnl/{date}` - Get P&L for date
  - `GET /api/valuation/pnl/{date}/unexplained` - High unexplained P&L
  - `POST /api/valuation/scenario` - Run scenario analysis
  - `GET /api/valuation/scenario/{scenarioId}` - Get scenario results

---

## 📁 Files Created

### Java Entities
```
src/main/java/com/trading/ctrm/pricing/
├── VolatilitySurface.java       - Vol surface header
├── VolatilityPoint.java         - Individual vol points
├── PnlExplain.java              - P&L attribution record
├── ValuationRun.java            - Batch run metadata
├── ValuationScenario.java       - Scenario definition
└── ScenarioResult.java          - Scenario analysis results
```

### Repositories
```
src/main/java/com/trading/ctrm/pricing/
├── VolatilitySurfaceRepository.java
├── PnlExplainRepository.java
├── ValuationRunRepository.java
├── ValuationScenarioRepository.java
└── ScenarioResultRepository.java
```

### Services
```
src/main/java/com/trading/ctrm/pricing/
├── BatchValuationService.java      - Portfolio revaluation
├── PnlAttributionService.java      - P&L calculation
└── ScenarioService.java            - Stress testing
```

### Controllers
```
src/main/java/com/trading/ctrm/pricing/
└── BatchValuationController.java   - REST API
```

### Database
```
phase2_schema.sql                   - Phase 2 DDL + seed data
```

---

## 🗄️ Database Schema

### Core Tables

**valuation_scenario**
- Scenario definitions (spot shocks, curve shifts, vol shocks)
- Parameterized configurations
- Audit trail (created_by, created_at)

**scenario_result**
- Per-trade scenario impact
- Base vs scenario MTM comparison
- P&L impact (absolute and percentage)

### Performance Indexes
- Trade + valuation date lookups
- P&L date range queries
- Unexplained P&L filters
- Scenario impact sorting

### Reporting Views
- `v_portfolio_pnl_summary` - Daily P&L with attribution
- `v_scenario_impact_summary` - Scenario statistics
- `v_valuation_run_summary` - Batch run performance

---

## 🔄 Workflow Examples

### 1. Batch Valuation Workflow
```
1. User triggers batch valuation via UI
2. Create ValuationRun record (RUNNING status)
3. Query trades by portfolio filter
4. Parallel valuation (10 threads)
   - Build ValuationContext for each trade
   - Call PricingEngine.price()
   - Save ValuationResult
5. Update run counts (successful/failed)
6. Set status to COMPLETED
```

### 2. Daily P&L Workflow
```
1. User triggers P&L calculation
2. Query all BOOKED trades
3. For each trade:
   - Get valuations for T-1 and T
   - Calculate total P&L = MTM_T - MTM_T-1
   - Attribute to risk factors:
     * Spot move (delta effect)
     * Curve move (forward pricing)
     * Vol move (vega effect)
     * Time decay (theta)
   - Calculate unexplained residual
4. Save PnlExplain records
5. Generate portfolio summary
```

### 3. Scenario Analysis Workflow
```
1. User defines scenario (type + parameters)
2. Create ValuationScenario record
3. For each trade in portfolio:
   - Get base case valuation
   - Build shocked ValuationContext
     * Apply spot shock to MarketContext
     * Apply curve shift to PricingContext
     * Apply vol shock to RiskContext
   - Calculate scenario valuation
   - Compute impact (scenario - base)
4. Save ScenarioResult records
5. Aggregate portfolio impact
```

---

## 🚀 API Usage Examples

### Run Batch Valuation
```bash
curl -X POST http://localhost:8080/api/valuation/batch \
  -H "Content-Type: application/json" \
  -d '{
    "valuationDate": "2026-01-02",
    "portfolioFilter": "ALL",
    "startedBy": "RISK_TEAM"
  }'
```

**Response:**
```json
{
  "runId": 1,
  "status": "COMPLETED",
  "totalTrades": 150,
  "successfulCount": 148,
  "failedCount": 2,
  "startedAt": "2026-01-02T10:00:00",
  "completedAt": "2026-01-02T10:02:15"
}
```

### Calculate Daily P&L
```bash
curl -X POST http://localhost:8080/api/valuation/pnl/calculate \
  -H "Content-Type: application/json" \
  -d '{
    "pnlDate": "2026-01-02"
  }'
```

**Response:**
```json
{
  "pnlDate": "2026-01-02",
  "totalPnl": 125000.50,
  "tradeCount": 148,
  "details": [...]
}
```

### Run Scenario Analysis
```bash
curl -X POST http://localhost:8080/api/valuation/scenario \
  -H "Content-Type: application/json" \
  -d '{
    "scenarioName": "10% Spot Shock Down",
    "scenarioType": "SPOT_SHOCK",
    "baseDate": "2026-01-02",
    "parameters": "{\"spotShock\": -10}",
    "portfolioFilter": "POWER_TRADING",
    "createdBy": "RISK_MANAGER"
  }'
```

**Response:**
```json
{
  "scenarioId": 5,
  "scenarioName": "10% Spot Shock Down",
  "totalImpact": -2500000.00,
  "topImpacts": [
    {
      "tradeId": 1234,
      "baseMtm": 500000,
      "scenarioMtm": 350000,
      "pnlImpact": -150000,
      "pnlImpactPct": -30.0
    }
  ]
}
```

---

## 🎓 Key Implementation Details

### 1. Parallel Processing
- `ExecutorService` with 10-thread pool
- `CompletableFuture` for async valuation
- Thread-safe repository operations

### 2. P&L Attribution Algorithm
```java
// Total P&L
totalPnl = MTM_T1 - MTM_T0

// Attribution
spotPnl = SpotMTM_T1 - SpotMTM_T0
curvePnl = ForwardMTM_T1 - ForwardMTM_T0
volPnl = (approx from vega)
thetaPnl = theta_T0
unexplained = totalPnl - (spotPnl + curvePnl + volPnl + thetaPnl)
```

### 3. Scenario Context Building
```java
switch (scenarioType) {
    case "SPOT_SHOCK":
        // Override spot price in MarketContext
        // Pass shock percentage to PricingContext
        break;
    case "CURVE_SHIFT":
        // Shift all forward curve points
        break;
    case "VOL_SHOCK":
        // Shock implied volatility
        break;
}
```

---

## 🧪 Testing

### SQL Execution
```bash
# Create Phase 2 tables and seed data
psql -U postgres -d ctrm_db -f phase2_schema.sql
```

### Verify Tables
```sql
-- Check tables
\dt valuation_scenario
\dt scenario_result

-- Check views
\dv v_portfolio_pnl_summary
\dv v_scenario_impact_summary
\dv v_valuation_run_summary

-- Check sample data
SELECT * FROM valuation_scenario;
SELECT * FROM volatility_surface;
```

### API Testing
1. Start Spring Boot application
2. Run batch valuation for current date
3. Calculate P&L (requires T-1 valuations)
4. Run spot shock scenario
5. Check results via GET endpoints

---

## 📊 Sample Data Provided

### Scenarios
- 10% Spot Shock Up
- 10% Spot Shock Down
- Parallel Curve Shift +$5
- Vol Shock +20%
- 2008 Financial Crisis (historical)

### Volatility Surface
- POWER_ATM_VOL surface for current date
- ATM vols at 1M, 3M, 6M, 1Y, 2Y tenors

---

## 🔗 Integration with Phase 1

Phase 2 builds on Phase 1 infrastructure:
- Uses `ValuationContext` from Phase 1
- Extends `PricingEngine` interface
- Leverages `ValuationResult` entity
- Integrates with `MarketCurveService`

---

## 🎯 Next Steps

### Phase 3 (Future)
1. **Trade Versioning**
   - Amendment history tracking
   - Historical state reconstruction
   - Audit trail with diffs

2. **Advanced P&L**
   - Intraday P&L
   - Cross-currency P&L
   - Tax lot accounting

3. **Risk Limits**
   - VaR calculation
   - Position limits
   - Concentration limits

4. **UI Development**
   - Batch valuation dashboard
   - P&L viewer with drill-down
   - Scenario builder with charts
   - Unexplained P&L alerts

---

## ✅ Phase 2 Checklist

- [x] Volatility surface entities and repositories
- [x] P&L explain entity and service
- [x] Valuation run tracking
- [x] Batch valuation service with parallel processing
- [x] P&L attribution service
- [x] Scenario framework (entities, service)
- [x] REST API with all endpoints
- [x] SQL schema with indexes and views
- [x] Sample data for testing
- [x] Documentation

---

**Phase 2 Complete! Enterprise-grade pricing infrastructure ready for production.**
