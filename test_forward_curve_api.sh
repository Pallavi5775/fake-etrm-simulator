# Forward Curve API Test Commands

## 1. Quick Test - Get existing curve for PWR-Q1-25
curl "http://localhost:8080/api/forward-curves?instrumentCode=PWR-Q1-25&deliveryDate=2026-01-02"

## 2. Get all curves for PWR-Q1-25
curl http://localhost:8080/api/forward-curves/instrument/PWR-Q1-25

## 3. Create/Update a single curve point
curl -X POST http://localhost:8080/api/forward-curves \
  -H "Content-Type: application/json" \
  -d "{\"instrumentCode\": \"PWR-Q1-25\", \"deliveryDate\": \"2026-01-10\", \"price\": 62.50}"

## 4. Bulk upload
curl -X POST http://localhost:8080/api/forward-curves/bulk \
  -H "Content-Type: application/json" \
  -d "[{\"instrumentCode\": \"PWR-Q1-25\", \"deliveryDate\": \"2026-01-15\", \"price\": 63.00}, {\"instrumentCode\": \"PWR-Q1-25\", \"deliveryDate\": \"2026-01-16\", \"price\": 63.25}]"

## 5. Get all instruments with curves
curl http://localhost:8080/api/forward-curves/instruments

## 6. Test P&L calculation (should now work)
curl http://localhost:8080/api/trades/TRD-20260103-0001/pnl

## 7. Delete a curve point
curl -X DELETE "http://localhost:8080/api/forward-curves?instrumentCode=PWR-Q1-25&deliveryDate=2026-01-10"
