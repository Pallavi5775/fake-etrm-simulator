# Forward Curve Management Implementation Summary

## Problem Statement
The P&L calculation was failing with error:
```
Failed to calculate P&L for trade TRD-20260103-0001: 
Forward curve not found for PWR-Q1-25 on 2026-01-02
```

You requested a **mechanism to create and manage forward curves** instead of hard-coding them in the backend.

## Solution Implemented

### Created REST API System for Forward Curve Management

#### 1. New Files Created

**ForwardCurveController.java** (`src/main/java/com/trading/ctrm/marketdata/`)
- Complete REST API with 7 endpoints
- Create, update, bulk upload, query, and delete operations
- Integration with existing Instrument repository

**ForwardCurveRequest.java** (`src/main/java/com/trading/ctrm/marketdata/`)
- Request DTO with fields: instrumentCode, deliveryDate, price

**ForwardCurveResponse.java** (`src/main/java/com/trading/ctrm/marketdata/`)
- Response DTO with fields: id, instrumentCode, deliveryDate, price, curveDate

**BulkUploadResponse.java** (`src/main/java/com/trading/ctrm/marketdata/`)
- Summary response for bulk uploads: total, created, updated, errors

#### 2. Updated Files

**ForwardCurve.java** (Entity)
- Fixed table name to `forward_curve` with schema `ctrm`
- Changed fetch type from LAZY to EAGER for instrument
- Added `curveDate` field to track when curve was published
- Added all setters (previously only had getters)
- Added proper column mappings

**ForwardCurveRepository.java**
- Added `findByInstrumentOrderByDeliveryDate()` for getting all curves per instrument
- Added `findDistinctInstruments()` JPQL query to list instruments with curves
- Added @Repository annotation

**InstrumentRepository.java**
- Added `findOptionalByInstrumentCode()` method that returns Optional<Instrument>

#### 3. Documentation Created

**FORWARD_CURVE_API_GUIDE.md**
- Complete API documentation with examples
- Usage workflows and best practices
- Integration guidance for UI, Python, Excel
- Error handling documentation

**test_forward_curve_api.sh**
- Quick test commands for all endpoints
- Ready-to-run curl examples

## API Endpoints Summary

### 1. Create/Update Single Point
```
POST /api/forward-curves
Body: {"instrumentCode": "PWR-Q1-25", "deliveryDate": "2026-01-02", "price": 60.50}
```

### 2. Bulk Upload
```
POST /api/forward-curves/bulk
Body: [{"instrumentCode": "PWR-Q1-25", "deliveryDate": "2026-01-02", "price": 60.50}, ...]
```

### 3. Get Single Point
```
GET /api/forward-curves?instrumentCode=PWR-Q1-25&deliveryDate=2026-01-02
```

### 4. Get All Points for Instrument
```
GET /api/forward-curves/instrument/PWR-Q1-25
```

### 5. List All Instruments with Curves
```
GET /api/forward-curves/instruments
```

### 6. Delete Curve Point
```
DELETE /api/forward-curves?instrumentCode=PWR-Q1-25&deliveryDate=2026-01-02
```

## How to Use

### Step 1: Restart Application
```bash
# Stop current application (Ctrl+C)
mvn clean install
mvn spring-boot:run
```

### Step 2: Verify Existing Data
The database already has forward curve data from previous setup:
```bash
curl http://localhost:8080/api/forward-curves/instrument/PWR-Q1-25
```

### Step 3: Test P&L Calculation
```bash
# Now this should work without errors
curl http://localhost:8080/api/trades/TRD-20260103-0001/pnl
```

### Step 4: Add New Curves
```bash
# Single point
curl -X POST http://localhost:8080/api/forward-curves \
  -H "Content-Type: application/json" \
  -d '{"instrumentCode": "PWR-Q1-25", "deliveryDate": "2026-01-10", "price": 62.50}'

# Bulk upload
curl -X POST http://localhost:8080/api/forward-curves/bulk \
  -H "Content-Type: application/json" \
  -d '[
    {"instrumentCode": "PWR-Q1-25", "deliveryDate": "2026-01-15", "price": 63.00},
    {"instrumentCode": "PWR-Q1-25", "deliveryDate": "2026-01-16", "price": 63.25}
  ]'
```

## Benefits of This Solution

### 1. **Self-Service Market Data Management**
- Users can upload curves via API without backend code changes
- No need for database SQL scripts
- Real-time updates

### 2. **Multiple Integration Options**
- **REST API**: Direct calls from any HTTP client
- **UI Integration**: Build React/Angular screens for curve management
- **Python Scripts**: Automated curve uploads from market data providers
- **Excel/VBA**: Traders can maintain curves in Excel and push to system
- **CSV Upload**: Bulk import from files

### 3. **Operational Flexibility**
- Update curves multiple times per day
- Track curve history via `curveDate` field
- Easy to query what curves exist
- Bulk operations for efficiency

### 4. **Error Prevention**
- Clear error messages when instrument doesn't exist
- Create/update logic prevents duplicates
- Validation at API layer

## Database Schema

The system uses existing `forward_curve` table:
```sql
TABLE ctrm.forward_curve (
    id SERIAL PRIMARY KEY,
    instrument_id BIGINT REFERENCES ctrm.instruments(id),
    delivery_date DATE NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    curve_date DATE,
    created_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(instrument_id, delivery_date)
)
```

## Next Steps

### Immediate
1. ✅ **Restart application** to load new APIs
2. ✅ **Test existing curves** work with P&L calculation
3. ✅ **Try bulk upload** to add more dates

### Short Term
1. **Build UI screen** for curve management:
   - Table view of curves by instrument
   - Add/Edit/Delete functionality
   - CSV file upload
   - Curve visualization charts

2. **Automate daily updates**:
   - Python script to fetch from market data provider
   - Scheduled job to upload curves each morning
   - Alert if curves missing for active instruments

3. **Add validation rules**:
   - Price range checks
   - Business day validation
   - Contango/backwardation warnings

### Long Term
1. **Curve versioning**: Keep historical curves for audit
2. **Curve interpolation**: Auto-fill missing dates
3. **Multi-curve support**: Different curves per delivery location
4. **Curve analytics**: Track curve shifts, spreads

## Testing Checklist

- [ ] Restart application successfully
- [ ] GET existing curves returns data
- [ ] Single trade P&L calculation works
- [ ] Create single curve point via POST
- [ ] Bulk upload multiple curves
- [ ] Query curves by instrument
- [ ] List all instruments with curves
- [ ] Delete curve point
- [ ] Error handling for invalid instrument
- [ ] Error handling for missing curve

## Files Reference

### Code Files
- `src/main/java/com/trading/ctrm/marketdata/ForwardCurveController.java` (NEW)
- `src/main/java/com/trading/ctrm/marketdata/ForwardCurveRequest.java` (NEW)
- `src/main/java/com/trading/ctrm/marketdata/ForwardCurveResponse.java` (NEW)
- `src/main/java/com/trading/ctrm/marketdata/BulkUploadResponse.java` (NEW)
- `src/main/java/com/trading/ctrm/trade/ForwardCurve.java` (UPDATED)
- `src/main/java/com/trading/ctrm/trade/ForwardCurveRepository.java` (UPDATED)
- `src/main/java/com/trading/ctrm/trade/InstrumentRepository.java` (UPDATED)

### Documentation
- `FORWARD_CURVE_API_GUIDE.md` - Complete API documentation
- `test_forward_curve_api.sh` - Test commands

## Common Use Cases

### Daily Market Data Update
```bash
# Each morning, upload today's curves
curl -X POST http://localhost:8080/api/forward-curves/bulk \
  -H "Content-Type: application/json" \
  -d @daily_curves.json
```

### Add New Instrument
```bash
# First create instrument via /api/instruments
# Then add forward curves
curl -X POST http://localhost:8080/api/forward-curves/bulk \
  -H "Content-Type: application/json" \
  -d '[
    {"instrumentCode": "NEW-INST", "deliveryDate": "2026-02-01", "price": 50.00},
    {"instrumentCode": "NEW-INST", "deliveryDate": "2026-02-02", "price": 50.25}
  ]'
```

### Correct Bad Price
```bash
# Update existing curve point
curl -X POST http://localhost:8080/api/forward-curves \
  -H "Content-Type: application/json" \
  -d '{"instrumentCode": "PWR-Q1-25", "deliveryDate": "2026-01-10", "price": 61.50}'
```

### Check Coverage
```bash
# See what instruments have curves
curl http://localhost:8080/api/forward-curves/instruments

# Get all dates for specific instrument
curl http://localhost:8080/api/forward-curves/instrument/PWR-Q1-25
```

## Troubleshooting

### "Instrument not found" error
- Check instrument code matches exactly (case-sensitive)
- Query `/api/instruments` to see available instruments
- Create instrument first if it doesn't exist

### Curve not being used in P&L
- Verify curve exists: `GET /api/forward-curves/instrument/{code}`
- Check delivery date matches trade date
- Restart application if curves added before app started

### Bulk upload showing errors
- Check response to see how many succeeded vs failed
- Each error usually means instrument not found
- Fix instrument codes and retry

## Support

For issues or questions:
1. Check logs: `tail -f logs/application.log`
2. Verify database: `psql -U postgres -d ctrm_db`
3. Review API guide: `FORWARD_CURVE_API_GUIDE.md`
