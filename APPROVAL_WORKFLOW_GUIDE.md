# Approval Workflow Implementation Guide

## Overview
The system implements a complete approval workflow where consultants/admins can create rules that control trade approval routing based on conditions.

## Implementation Status: ✅ FULLY IMPLEMENTED

### Components

#### 1. **Rule Creation (Consultant/Admin)**
- **UI**: `ApprovalRulesConfig.jsx`
- **API**: `POST /api/approval-rules`
- **Features**:
  - Define rule name and trigger event (TRADE_BOOK, AMEND, etc.)
  - Add multiple conditions (quantity > 1000, counterparty = X, etc.)
  - Configure multi-level routing (Level 1: RISK, Level 2: HEAD_TRADER, etc.)
  - Version management (create drafts, activate, create new versions)

#### 2. **Trade Booking (Trader)**
- **Service**: `TradeService.bookFromTemplate()`
- **Flow**:
  1. Trader books a trade
  2. System creates TradeContext with trade details
  3. ApprovalRuleEngine evaluates all active rules
  4. If rule matches → Trade status set to PENDING_APPROVAL
  5. If no rule matches → Trade auto-approved (status = APPROVED)

#### 3. **Rule Evaluation Engine**
- **Service**: `ApprovalRuleEngine`
- **Components**:
  - `FieldResolver` - Extracts field values from trade (quantity, counterparty, etc.)
  - `OperatorEvaluator` - Evaluates conditions (>, <, ==, !=, CONTAINS, etc.)
- **Logic**:
  - Fetches all ACTIVE rules for trigger event
  - Ordered by priority
  - ALL conditions must match for rule to apply
  - Returns first matching rule

#### 4. **Approval Execution**
- **Service**: `ApprovalExecutionService`
- **Creates**: `ApprovalTask` entity
- **Tracks**:
  - Which trade needs approval
  - Which rule triggered the approval
  - Current approval role and level
  - Status (PENDING, APPROVED, REJECTED)

#### 5. **Approval Action (Risk/Manager)**
- **API**: 
  - `POST /api/trades/{tradeId}/approve`
  - `POST /api/trades/{tradeId}/reject`
- **Service**: `TradeLifecycleEngine`
- **Flow**:
  - Verifies approver has correct role
  - Updates trade status
  - Creates audit trail (TradeEvent)
  - If multi-level, routes to next level

## Workflow Example

### Scenario: High Value Trade Approval

**Step 1: Consultant Creates Rule**
```json
{
  "ruleName": "High Value Trade Approval",
  "triggerEvent": "TRADE_BOOK",
  "priority": 1,
  "conditions": [
    {
      "fieldCode": "quantity",
      "operator": ">",
      "value1": "1000"
    }
  ],
  "routing": [
    {
      "approvalRole": "RISK",
      "approvalLevel": 1
    }
  ]
}
```

**Step 2: Trader Books Trade**
```javascript
POST /api/trades/book-from-template/1
{
  "quantity": 1500,
  "buySell": "BUY",
  "counterparty": "SHELL",
  "portfolio": "POWER_DESK"
}
```

**System Actions:**
1. ✅ Trade created with status = CREATED
2. ✅ ApprovalRuleEngine evaluates: quantity (1500) > 1000 → MATCH
3. ✅ Trade status changed to PENDING_APPROVAL
4. ✅ Trade.pendingApprovalRole = "RISK"
5. ✅ ApprovalTask created
6. ✅ Trade NOT executable until approved

**Step 3: Risk Manager Approves**
```javascript
POST /api/trades/TRD-123/approve
{
  "approverRole": "RISK",
  "comments": "Approved - within risk limits"
}
```

**System Actions:**
1. ✅ Verifies user has RISK role
2. ✅ Trade status changed to APPROVED
3. ✅ TradeEvent created for audit
4. ✅ ApprovalTask status = COMPLETED
5. ✅ Trade now executable

## Multi-Level Approval Example

**Rule Configuration:**
```
Condition: quantity > 10000
Routing:
  Level 1: SENIOR_TRADER
  Level 2: HEAD_TRADER
  Level 3: CFO
```

**Workflow:**
1. Trader books 15000 units
2. Status = PENDING_APPROVAL, pendingRole = SENIOR_TRADER
3. Senior Trader approves → pendingRole = HEAD_TRADER
4. Head Trader approves → pendingRole = CFO
5. CFO approves → status = APPROVED

## Database Schema

### Core Tables
1. `approval_rule` - Rule definitions
2. `approval_rule_condition` - Rule conditions (what triggers it)
3. `approval_routing` - Approval chain (who approves)
4. `approval_task` - Active approval workflows
5. `trade` - Trade with pendingApprovalRole field
6. `trade_event` - Audit trail

## API Endpoints

### Rule Management (Consultant)
- `GET /api/approval-rules` - List all rules
- `POST /api/approval-rules` - Create rule (draft)
- `POST /api/approval-rules/{id}/activate` - Activate rule
- `POST /api/approval-rules/{id}/new-version` - Create new version

### Trade Booking (Trader)
- `POST /api/trades/book-from-template/{templateId}` - Book trade with approval check

### Approval Actions (Manager)
- `POST /api/trades/{tradeId}/approve` - Approve trade
- `POST /api/trades/{tradeId}/reject` - Reject trade
- `GET /api/trades?status=PENDING_APPROVAL` - List pending approvals

## Testing Instructions

1. **Run SQL scripts:**
   ```sql
   -- Create schemas
   \i approval_rules_schema.sql
   \i approval_workflow_sample.sql
   ```

2. **Create approval rule via UI:**
   - Login as admin
   - Go to Approval Rules Config
   - Create rule: quantity > 1000 → RISK approval

3. **Book trade as trader:**
   - Login as trader1
   - Book trade with quantity = 1500
   - Verify status = PENDING_APPROVAL

4. **Approve as risk manager:**
   - Login as risk_user
   - View pending approvals
   - Approve trade
   - Verify status = APPROVED

## Status Summary

✅ **Rule Creation** - Complete
✅ **Rule Evaluation** - Complete
✅ **Approval Routing** - Complete
✅ **Multi-Level Approval** - Complete
✅ **Status Management** - Complete
✅ **Audit Trail** - Complete
✅ **UI Components** - Complete
✅ **API Endpoints** - Complete

## Next Enhancements (Optional)

1. **Email Notifications** - Notify approvers when trade pending
2. **SLA Tracking** - Track time to approval
3. **Delegation** - Allow approvers to delegate
4. **Bulk Approval** - Approve multiple trades at once
5. **Rule Testing** - Test rules before activation
6. **Analytics** - Dashboard showing approval metrics
