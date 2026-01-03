# Forward Curve Management API Guide

## Overview
This system provides REST APIs to create, update, query, and delete forward curve data for market pricing.

## Endpoints

### 1. Create or Update Single Curve Point
**POST** `/api/forward-curves`

Creates a new forward curve point or updates if it already exists for the instrument/date combination.

**Request Body:**
```json
{
  "instrumentCode": "PWR-Q1-25",
  "deliveryDate": "2026-01-02",
  "price": 60.50
}
```

**Response:**
```json
{
  "id": 1,
  "instrumentCode": "PWR-Q1-25",
  "deliveryDate": "2026-01-02",
  "price": 60.50,
  "curveDate": "2026-01-03"
}
```

**Example (curl):**
```bash
curl -X POST http://localhost:8080/api/forward-curves \
  -H "Content-Type: application/json" \
  -d '{
    "instrumentCode": "PWR-Q1-25",
    "deliveryDate": "2026-01-02",
    "price": 60.50
  }'
```

---

### 2. Bulk Upload Curve Points
**POST** `/api/forward-curves/bulk`

Upload multiple curve points in one request. Automatically creates new entries or updates existing ones.

**Request Body:**
```json
[
  {
    "instrumentCode": "PWR-Q1-25",
    "deliveryDate": "2026-01-02",
    "price": 60.50
  },
  {
    "instrumentCode": "PWR-Q1-25",
    "deliveryDate": "2026-01-03",
    "price": 61.00
  },
  {
    "instrumentCode": "PWR-Q1-25",
    "deliveryDate": "2026-01-04",
    "price": 61.25
  }
]
```

**Response:**
```json
{
  "total": 3,
  "created": 2,
  "updated": 1,
  "errors": 0,
  "message": "Processed 3 records: 2 created, 1 updated, 0 errors"
}
```

**Example (curl):**
```bash
curl -X POST http://localhost:8080/api/forward-curves/bulk \
  -H "Content-Type: application/json" \
  -d '[
    {"instrumentCode": "PWR-Q1-25", "deliveryDate": "2026-01-02", "price": 60.50},
    {"instrumentCode": "PWR-Q1-25", "deliveryDate": "2026-01-03", "price": 61.00}
  ]'
```

---

### 3. Get Single Curve Point
**GET** `/api/forward-curves?instrumentCode={code}&deliveryDate={date}`

Retrieve a specific forward curve point.

**Query Parameters:**
- `instrumentCode` - The instrument code (e.g., "PWR-Q1-25")
- `deliveryDate` - The delivery date (format: YYYY-MM-DD)

**Response:**
```json
{
  "id": 1,
  "instrumentCode": "PWR-Q1-25",
  "deliveryDate": "2026-01-02",
  "price": 60.50,
  "curveDate": "2026-01-03"
}
```

**Example:**
```bash
curl "http://localhost:8080/api/forward-curves?instrumentCode=PWR-Q1-25&deliveryDate=2026-01-02"
```

---

### 4. Get All Curve Points for Instrument
**GET** `/api/forward-curves/instrument/{instrumentCode}`

Get all forward curve points for a specific instrument, ordered by delivery date.

**Response:**
```json
[
  {
    "id": 1,
    "instrumentCode": "PWR-Q1-25",
    "deliveryDate": "2026-01-02",
    "price": 60.50,
    "curveDate": "2026-01-03"
  },
  {
    "id": 2,
    "instrumentCode": "PWR-Q1-25",
    "deliveryDate": "2026-01-03",
    "price": 61.00,
    "curveDate": "2026-01-03"
  }
]
```

**Example:**
```bash
curl http://localhost:8080/api/forward-curves/instrument/PWR-Q1-25
```

---

### 5. Get All Instruments with Curves
**GET** `/api/forward-curves/instruments`

Get a list of all instrument codes that have forward curve data.

**Response:**
```json
[
  "PWR-Q1-25",
  "GAS-JAN-26",
  "OIL-FEB-26"
]
```

**Example:**
```bash
curl http://localhost:8080/api/forward-curves/instruments
```

---

### 6. Delete Curve Point
**DELETE** `/api/forward-curves?instrumentCode={code}&deliveryDate={date}`

Delete a specific forward curve point.

**Query Parameters:**
- `instrumentCode` - The instrument code
- `deliveryDate` - The delivery date

**Response:** 204 No Content

**Example:**
```bash
curl -X DELETE "http://localhost:8080/api/forward-curves?instrumentCode=PWR-Q1-25&deliveryDate=2026-01-02"
```

---

## Usage Workflow

### Initial Setup for Your Trade
Your trade TRD-20260103-0001 needs forward curve data. Here's how to add it:

1. **Single point upload:**
```bash
curl -X POST http://localhost:8080/api/forward-curves \
  -H "Content-Type: application/json" \
  -d '{
    "instrumentCode": "PWR-Q1-25",
    "deliveryDate": "2026-01-02",
    "price": 60.50
  }'
```

2. **Or bulk upload for entire curve:**
```bash
curl -X POST http://localhost:8080/api/forward-curves/bulk \
  -H "Content-Type: application/json" \
  -d '[
    {"instrumentCode": "PWR-Q1-25", "deliveryDate": "2026-01-02", "price": 60.50},
    {"instrumentCode": "PWR-Q1-25", "deliveryDate": "2026-01-03", "price": 61.00},
    {"instrumentCode": "PWR-Q1-25", "deliveryDate": "2026-01-04", "price": 61.25},
    {"instrumentCode": "PWR-Q1-25", "deliveryDate": "2026-01-05", "price": 61.75}
  ]'
```

3. **Verify the data:**
```bash
curl http://localhost:8080/api/forward-curves/instrument/PWR-Q1-25
```

4. **Now calculate P&L:**
```bash
# This will now work without errors
curl http://localhost:8080/api/trades/TRD-20260103-0001/pnl
```

---

## Daily Market Update Workflow

1. **Daily curve update (morning):**
   - Upload today's forward curves via bulk endpoint
   - System automatically timestamps with `curveDate`

2. **Calculate P&L:**
   - Run batch P&L calculation
   - System uses latest curves for each instrument

3. **Query for missing curves:**
   - Check `/api/forward-curves/instruments` to see what's loaded
   - Add missing instruments as needed

---

## Integration Points

### Frontend Integration
You can build a UI screen with:
- Upload CSV file for bulk curve upload
- Table view of current curves by instrument
- Edit individual curve points
- Delete old/incorrect curves

### Python Script Integration
```python
import requests
import pandas as pd

# Load curves from CSV
df = pd.read_csv('forward_curves.csv')

# Convert to JSON array
curves = df.to_dict('records')

# Upload to API
response = requests.post(
    'http://localhost:8080/api/forward-curves/bulk',
    json=curves
)

print(response.json())
```

### Excel/VBA Integration
Use Excel to maintain curves and push via API calls from VBA macros.

---

## Error Handling

**Instrument not found:**
```json
{
  "error": "Instrument not found: INVALID-CODE"
}
```

**Curve not found:**
```json
{
  "error": "Forward curve not found for PWR-Q1-25 on 2026-01-02"
}
```

**Bulk upload with errors:**
```json
{
  "total": 5,
  "created": 3,
  "updated": 1,
  "errors": 1,
  "message": "Processed 5 records: 3 created, 1 updated, 1 errors"
}
```

---

## Best Practices

1. **Daily Updates:**
   - Upload full curve each day using bulk endpoint
   - System handles create/update automatically

2. **Curve Management:**
   - Keep historical curves (don't delete old data)
   - Use `curveDate` to track when curve was published

3. **Data Validation:**
   - Verify instrument codes exist before uploading
   - Check for reasonable price ranges
   - Use GET endpoint to verify uploads

4. **Performance:**
   - Use bulk endpoint for multiple points
   - Single endpoint fine for real-time updates

5. **Monitoring:**
   - Check `/api/forward-curves/instruments` regularly
   - Ensure all active instruments have curves
   - Monitor for missing delivery dates
