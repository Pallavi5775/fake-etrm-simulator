# Multi-Level Approval Implementation Summary

## What Was Implemented

The system now supports **sequential multi-level approval workflows** where trades progress through multiple approval stages defined by approval rules.

## Files Changed

### 1. Trade.java
**Added Fields:**
- `currentApprovalLevel` (Integer): Tracks which approval level (1, 2, 3...) is currently required
- `matchedRuleId` (Long): Foreign key to the approval rule that was matched when the trade was booked

**Added Methods:**
- `getCurrentApprovalLevel()` / `setCurrentApprovalLevel(Integer)`
- `getMatchedRuleId()` / `setMatchedRuleId(Long)`

### 2. TradeService.java
**Modified Method:** `bookFromTemplate()`

**Changes:**
- When an approval rule matches:
  - Sets `trade.setMatchedRuleId(rule.getRuleId())` to store which rule was matched
  - Sets `trade.setCurrentApprovalLevel(1)` to start at level 1
  - Filters routing for level 1: `.filter(r -> r.getApprovalLevel() == 1)`
  - Sets `pendingApprovalRole` from level 1 routing

**Before:**
```java
trade.setPendingApprovalRole(
    rule.getRouting().stream()
        .min(Comparator.comparingInt(ApprovalRouting::getApprovalLevel))
        .map(ApprovalRouting::getApprovalRole)
        .orElse(null)
);
```

**After:**
```java
trade.setMatchedRuleId(rule.getRuleId());
trade.setCurrentApprovalLevel(1);
trade.setPendingApprovalRole(
    rule.getRouting().stream()
        .filter(r -> r.getApprovalLevel() == 1)
        .findFirst()
        .map(ApprovalRouting::getApprovalRole)
        .orElse(null)
);
```

### 3. TradeLifecycleEngine.java
**Added Dependency:** `ApprovalRoutingService`

**Modified Method:** `approveTrade()`

**Changes:**
- After validating user role, checks if more approval levels exist
- If next level exists:
  - Updates `currentApprovalLevel` to next level
  - Updates `pendingApprovalRole` to next level's role
  - Keeps status as `PENDING_APPROVAL`
- If no more levels:
  - Sets status to `APPROVED`
  - Clears `pendingApprovalRole` and `currentApprovalLevel`

**New Logic:**
```java
if (trade.getMatchedRuleId() != null && trade.getCurrentApprovalLevel() != null) {
    Optional<ApprovalRouting> nextLevel = approvalRoutingService.getNextLevel(
        trade.getMatchedRuleId(),
        trade.getCurrentApprovalLevel()
    );

    if (nextLevel.isPresent()) {
        // Move to next approval level
        trade.setCurrentApprovalLevel(nextLevel.get().getApprovalLevel());
        trade.setPendingApprovalRole(nextLevel.get().getApprovalRole());
        // Status remains PENDING_APPROVAL
    } else {
        // No more levels - fully approved
        trade.setStatus(TradeStatus.APPROVED);
        trade.setPendingApprovalRole(null);
        trade.setCurrentApprovalLevel(null);
    }
}
```

### 4. ApprovalRoutingService.java (NEW)
**Purpose:** Service to manage approval level progression

**Methods:**
- `getNextLevel(ruleId, currentLevel)`: Returns `Optional<ApprovalRouting>` for next level
- `hasMoreLevels(ruleId, currentLevel)`: Returns boolean indicating if more levels exist

**Key Implementation:**
```java
public Optional<ApprovalRouting> getNextLevel(Long matchedRuleId, Integer currentLevel) {
    if (matchedRuleId == null || currentLevel == null) {
        return Optional.empty();
    }

    ApprovalRule rule = approvalRuleRepository.findById(matchedRuleId)
            .orElse(null);
    
    if (rule == null || rule.getRouting() == null) {
        return Optional.empty();
    }

    int nextLevel = currentLevel + 1;
    
    return rule.getRouting().stream()
            .filter(r -> r.getApprovalLevel() != null && r.getApprovalLevel() == nextLevel)
            .findFirst();
}
```

## Database Changes

### Migration Script: `migration_add_approval_routing_to_trade.sql`

```sql
ALTER TABLE ctrm.trade 
ADD COLUMN current_approval_level INTEGER,
ADD COLUMN matched_rule_id BIGINT;

COMMENT ON COLUMN ctrm.trade.current_approval_level IS 'Tracks which approval level (1, 2, 3...) is currently required';
COMMENT ON COLUMN ctrm.trade.matched_rule_id IS 'Foreign key to approval_rule.rule_id that was matched when trade was booked';
```

## Documentation Created

### 1. MULTI_LEVEL_APPROVAL_GUIDE.md
- Complete overview of the feature
- Example workflows with 3-level approval
- Database schema documentation
- API reference
- Key classes and their responsibilities
- Testing instructions

### 2. test_multi_level_approval.sql
- Complete test script with sample data
- Creates users for each approval level
- Sets up a 3-level approval rule
- Verification queries
- Error scenario tests
- Cleanup scripts

## How It Works - Step by Step

### Step 1: Rule Setup (by Consultant)
```sql
INSERT INTO ctrm.approval_rule (rule_name, trigger_event, priority, active, status)
VALUES ('High Value Trade', 'TRADE_BOOK', 1, true, 'ACTIVE')
RETURNING rule_id; -- Returns 100

INSERT INTO ctrm.approval_routing (rule_id, approval_role, approval_level)
VALUES
    (100, 'SENIOR_TRADER', 1),
    (100, 'RISK', 2),
    (100, 'HEAD_TRADER', 3);
```

### Step 2: Trade Booking
```
Trader books trade with quantity > threshold
→ System matches rule #100
→ Sets matched_rule_id = 100
→ Sets current_approval_level = 1
→ Sets pending_approval_role = 'SENIOR_TRADER'
→ Status = PENDING_APPROVAL
```

### Step 3: Level 1 Approval
```
SENIOR_TRADER approves
→ System validates role matches 'SENIOR_TRADER' ✓
→ Looks up next level (level 2)
→ Sets current_approval_level = 2
→ Sets pending_approval_role = 'RISK'
→ Status remains PENDING_APPROVAL
```

### Step 4: Level 2 Approval
```
RISK approves
→ System validates role matches 'RISK' ✓
→ Looks up next level (level 3)
→ Sets current_approval_level = 3
→ Sets pending_approval_role = 'HEAD_TRADER'
→ Status remains PENDING_APPROVAL
```

### Step 5: Level 3 Approval (Final)
```
HEAD_TRADER approves
→ System validates role matches 'HEAD_TRADER' ✓
→ Looks up next level (none found)
→ Sets status = APPROVED
→ Clears pending_approval_role = NULL
→ Clears current_approval_level = NULL
```

## Key Benefits

✅ **Flexible**: Consultants can define any number of approval levels (1, 2, 3, 4+)
✅ **Role-Based**: Each level requires a specific role
✅ **Auditable**: System tracks which level is current in the trade table
✅ **Scalable**: No hardcoded approval logic - completely rule-driven
✅ **Sequential**: Enforces proper approval order (cannot skip levels)
✅ **Self-Approver Protection**: User who creates trade cannot approve if they have wrong role

## Error Handling

1. **Wrong Role**: If user's role doesn't match `pending_approval_role`
   - Returns: `400 Bad Request`
   - Message: "Approval requires role: X, but user has role: Y"

2. **Trade Not Pending**: If trade is not in `PENDING_APPROVAL` status
   - Returns: `400 Bad Request`
   - Message: "Trade is not pending approval: APPROVED"

3. **Missing Routing**: If rule exists but has no routing levels
   - Trade gets `pendingApprovalRole = null`
   - Requires manual intervention

## Testing Checklist

- [ ] Run migration script to add new columns
- [ ] Restart Spring Boot application
- [ ] Create test users with different roles
- [ ] Create approval rule with 2-3 levels
- [ ] Book a trade that matches the rule
- [ ] Verify initial state: level=1, pending=first role
- [ ] Approve as level 1 role
- [ ] Verify state: level=2, pending=second role
- [ ] Approve as level 2 role
- [ ] Verify progression (level 3 if exists, or APPROVED)
- [ ] Test error cases: wrong role, wrong status

## Integration Points

- **Frontend**: Update UI to show current approval level
- **Notifications**: Send alerts to users with pending approval role
- **Audit**: Track approval history in `trade_event` table
- **Reporting**: Query trades by approval level for dashboards

## Future Enhancements

- [ ] Parallel approval (multiple roles at same level)
- [ ] Conditional routing (skip level if condition met)
- [ ] Approval delegation (assign to specific user)
- [ ] Time-based escalation (auto-escalate if not approved in X hours)
- [ ] Approval comments/notes
- [ ] Rejection workflow (send back to previous level)

## Troubleshooting

**Issue:** Trade approved immediately instead of requiring multiple levels
- **Check:** Does rule have multiple routing levels?
- **Check:** Is `matched_rule_id` set on the trade?
- **Check:** Is `current_approval_level` = 1?

**Issue:** "No lifecycle rule configured" error
- **Check:** LifecycleRule table, not ApprovalRule (different tables!)
- **Solution:** Ensure lifecycle rules exist for CREATED → PENDING_APPROVAL transition

**Issue:** Null pointer when approving
- **Check:** ApprovalRoutingService is injected in TradeLifecycleEngine
- **Check:** Rule exists in database with correct rule_id

## Deployment Steps

1. Backup database
2. Run `migration_add_approval_routing_to_trade.sql`
3. Deploy application code
4. Verify with test script `test_multi_level_approval.sql`
5. Update frontend to show approval levels (optional)
6. Train consultants on multi-level rule setup
