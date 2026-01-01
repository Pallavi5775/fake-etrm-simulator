# Multi-Level Approval Workflow Guide

## Overview

The CTRM system now supports **multi-level approval routing** where trades follow a sequential approval hierarchy defined by approval rules. This allows consultants to configure complex approval workflows with multiple stages.

## How It Works

### 1. Rule Configuration

When creating an approval rule, you define multiple routing levels:

```sql
INSERT INTO ctrm.approval_routing (rule_id, approval_role, approval_level)
VALUES
    (1, 'SENIOR_TRADER', 1),  -- Level 1: Senior Trader must approve first
    (1, 'RISK', 2),            -- Level 2: Risk must approve second
    (1, 'HEAD_TRADER', 3);     -- Level 3: Head Trader approves last
```

### 2. Trade Booking

When a trade is booked from a template:
- The system evaluates all active approval rules
- If a rule matches, it stores the `matched_rule_id` in the trade
- Sets `current_approval_level = 1`
- Sets `pending_approval_role` to the role required for Level 1

### 3. Approval Progression

When a user approves at Level N:
1. System validates user's role matches `pending_approval_role`
2. Looks up the next routing level (N+1) from the matched rule
3. If Level N+1 exists:
   - Updates `current_approval_level = N+1`
   - Updates `pending_approval_role` to Level N+1's role
   - Trade remains `PENDING_APPROVAL`
4. If no more levels:
   - Sets trade status to `APPROVED`
   - Clears `pending_approval_role` and `current_approval_level`

## Example Workflow

### Scenario: High-Value Trade Requiring 3 Approvals

1. **Rule Setup** (by Consultant):
```sql
-- Create rule for trades > $1,000,000
INSERT INTO ctrm.approval_rule (rule_name, trigger_event, priority, active, status)
VALUES ('High Value Trade', 'TRADE_BOOK', 1, true, 'ACTIVE')
RETURNING rule_id; -- Returns rule_id = 5

-- Add condition: quantity > 1000000
INSERT INTO ctrm.approval_rule_condition (rule_id, field_code, operator, value1)
VALUES (5, 'QUANTITY', '>', '1000000');

-- Add 3-level routing
INSERT INTO ctrm.approval_routing (rule_id, approval_role, approval_level)
VALUES
    (5, 'SENIOR_TRADER', 1),
    (5, 'RISK', 2),
    (5, 'COMPLIANCE', 3);
```

2. **Trade Booking** (by pallavi_trade, role=TRADER):
```
POST /api/templates/1/book
{
  "quantity": 2000000,
  "buySell": "BUY",
  "counterparty": "SHELL",
  "portfolio": "POWER_TRADING"
}

Response:
{
  "tradeId": "abc-123",
  "status": "PENDING_APPROVAL",
  "currentApprovalLevel": 1,
  "pendingApprovalRole": "SENIOR_TRADER",
  "matchedRuleId": 5
}
```

3. **Level 1 Approval** (by john_senior, role=SENIOR_TRADER):
```
POST /api/trades/abc-123/approve
Headers: 
  X-User-Role: SENIOR_TRADER
  X-User-Name: john_senior

Response:
{
  "tradeId": "abc-123",
  "status": "PENDING_APPROVAL",  // Still pending!
  "currentApprovalLevel": 2,      // Advanced to level 2
  "pendingApprovalRole": "RISK"   // Now needs RISK approval
}
```

4. **Level 2 Approval** (by sarah_risk, role=RISK):
```
POST /api/trades/abc-123/approve
Headers: 
  X-User-Role: RISK
  X-User-Name: sarah_risk

Response:
{
  "tradeId": "abc-123",
  "status": "PENDING_APPROVAL",     // Still pending!
  "currentApprovalLevel": 3,         // Advanced to level 3
  "pendingApprovalRole": "COMPLIANCE" // Now needs COMPLIANCE approval
}
```

5. **Level 3 Approval** (by mike_compliance, role=COMPLIANCE):
```
POST /api/trades/abc-123/approve
Headers: 
  X-User-Role: COMPLIANCE
  X-User-Name: mike_compliance

Response:
{
  "tradeId": "abc-123",
  "status": "APPROVED",              // Finally approved!
  "currentApprovalLevel": null,
  "pendingApprovalRole": null
}
```

## Database Schema

### New Trade Fields

```sql
ALTER TABLE ctrm.trade 
ADD COLUMN current_approval_level INTEGER,
ADD COLUMN matched_rule_id BIGINT;
```

- `current_approval_level`: Tracks which level (1, 2, 3...) is currently required
- `matched_rule_id`: Foreign key to `approval_rule.rule_id` that was matched

### Approval Routing Table

```sql
CREATE TABLE ctrm.approval_routing (
    routing_id BIGSERIAL PRIMARY KEY,
    rule_id BIGINT REFERENCES ctrm.approval_rule(rule_id),
    approval_role VARCHAR(50) NOT NULL,
    approval_level INTEGER NOT NULL,
    UNIQUE(rule_id, approval_level)
);
```

## API Reference

### Approve Trade
```
POST /api/trades/{tradeId}/approve
Headers:
  X-User-Role: <user's role>
  X-User-Name: <username>
```

**Behavior:**
- Validates user's role matches `pending_approval_role`
- If more levels exist: advances to next level
- If no more levels: marks trade as APPROVED

**Error Cases:**
- `400 Bad Request`: Trade not in PENDING_APPROVAL status
- `403 Forbidden`: User's role doesn't match required role

### Query Pending Approvals

```
GET /api/trades/query?status=PENDING_APPROVAL
Headers:
  X-User-Role: <user's role>
```

Returns trades awaiting approval where `pending_approval_role` matches user's role.

## Key Classes

### ApprovalRoutingService
- `getNextLevel(ruleId, currentLevel)`: Retrieves next routing level
- `hasMoreLevels(ruleId, currentLevel)`: Checks if more levels exist

### TradeLifecycleEngine
- `approveTrade()`: Updated to handle multi-level progression
- Validates role, advances level, or marks approved

### TradeService
- `bookFromTemplate()`: Sets initial approval level and matched rule ID

## Testing Multi-Level Approval

1. **Create a 2-level rule** (simpler test case):
```sql
INSERT INTO ctrm.approval_rule (rule_name, trigger_event, priority, active, status)
VALUES ('Two Level Test', 'TRADE_BOOK', 1, true, 'ACTIVE')
RETURNING rule_id;

INSERT INTO ctrm.approval_rule_condition (rule_id, field_code, operator, value1)
VALUES (1, 'QUANTITY', '>', '1000');

INSERT INTO ctrm.approval_routing (rule_id, approval_role, approval_level)
VALUES
    (1, 'SENIOR_TRADER', 1),
    (1, 'RISK', 2);
```

2. **Book a trade** (as TRADER)
3. **Check trade state**: Should show level=1, pending=SENIOR_TRADER
4. **Approve as SENIOR_TRADER**: Should advance to level=2, pending=RISK
5. **Approve as RISK**: Should mark as APPROVED

## Benefits

✅ **Flexible**: Consultants define any number of approval levels
✅ **Role-Based**: Each level requires specific role
✅ **Auditable**: System tracks which level is current
✅ **Scalable**: No hardcoded approval logic - all rule-driven

## Migration Steps

1. Run database migration: `migration_add_approval_routing_to_trade.sql`
2. Restart application (Spring Boot will pick up new entity fields)
3. Create approval rules with routing levels
4. Test with sample trades
