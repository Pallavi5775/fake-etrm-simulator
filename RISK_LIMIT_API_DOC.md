# Risk/Credit Limit API Documentation

## 1. Check Limits (and Get Breaches)
**POST** `/api/risk/limits/check`

**Request:**
```
{
  "portfolio": "PORTFOLIO_NAME",
  "checkDate": "YYYY-MM-DD"
}
```

**Response:**
```
{
  "portfolio": "PORTFOLIO_NAME",
  "checkDate": "YYYY-MM-DD",
  "breachCount": 1,
  "breaches": [
    {
      "id": 123,
      "limitId": 1,
      "breachDate": "2026-01-04T12:00:00",
      "severity": "CRITICAL",
      "breachStatus": "ACTIVE",
      "currentValue": 1200000,
      "limitValue": 1000000
      // ...other fields
    }
  ]
}
```

---

## 2. Get All Active Limits
**GET** `/api/risk/limits`

**Response:**
```
[
  {
    "id": 1,
    "limitType": "CREDIT",
    "limitScope": "PORTFOLIO",
    "scopeValue": "PORTFOLIO_NAME",
    "limitValue": 1000000,
    "active": true
    // ...other fields
  }
  // ...more limits
]
```

---

## 3. Create a Limit
**POST** `/api/risk/limits`

**Request:**
```
{
  "limitType": "CREDIT",
  "limitScope": "PORTFOLIO",
  "scopeValue": "PORTFOLIO_NAME",
  "limitValue": 1000000,
  "active": true
  // ...other fields
}
```

**Response:**
Returns the created `RiskLimit` object (see above).

---

## 4. Get Active Breaches
**GET** `/api/risk/breaches/active`

**Response:**
Array of `RiskLimitBreach` objects (see above).

---

## 5. Get Critical Breaches
**GET** `/api/risk/breaches/critical`

**Response:**
Array of `RiskLimitBreach` objects with severity "CRITICAL".

---

## 6. Resolve a Breach
**POST** `/api/risk/breaches/{breachId}/resolve`

**Request:**
```
{
  "resolvedBy": "username",
  "notes": "Resolution details"
}
```

**Response:**
```
{
  "status": "resolved",
  "breachId": "123"
}
```

---

## 7. Get Risk Limit Metadata (Dropdown Options)
**GET** `/api/reference-data/risk-limit-metadata`

**Response:**
```
{
  "limitType": ["POSITION", "VAR", "DELTA", "CONCENTRATION", "CREDIT"],
  "limitScope": ["PORTFOLIO", "COMMODITY", "COUNTERPARTY"],
  "breachAction": ["ALERT", "BLOCK", "ESCALATE"],
  "limitUnit": ["MWh", "USD", "PERCENT", "GWh", "MMBtu", "Therm", "BBL", "MT", "tCO2e"]
}
```

---

## Entity Fields (Summary)

### RiskLimit (all fields)
- limitId: Long
- limitName: String
- limitType: String (e.g., POSITION, VAR, DELTA, CONCENTRATION)
- limitScope: String (e.g., PORTFOLIO, COMMODITY, COUNTERPARTY)
- scopeValue: String (portfolio/commodity/counterparty name)
- limitValue: BigDecimal
- warningThreshold: BigDecimal
- limitUnit: String (e.g., MWh, USD, PERCENT)
- active: Boolean
- breachAction: String (e.g., ALERT, BLOCK, ESCALATE)
- createdBy: String
- createdAt: LocalDateTime
- lastModifiedBy: String
- lastModifiedAt: LocalDateTime

### RiskLimitBreach (all fields)
- breachId: Long
- limitId: Long
- breachDate: LocalDateTime
- currentValue: BigDecimal
- limitValue: BigDecimal
- breachAmount: BigDecimal
- breachPercent: BigDecimal
- severity: String (e.g., WARNING, BREACH, CRITICAL)
- breachStatus: String (e.g., ACTIVE, RESOLVED, ACKNOWLEDGED)
- resolutionNotes: String
- resolvedBy: String
- resolvedAt: LocalDateTime
