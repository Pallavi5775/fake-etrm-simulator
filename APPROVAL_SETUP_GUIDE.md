# How to Apply Approval Rules to Trades

## Problem
Trade created by `pallavi_trade` can be approved by the same user. This shouldn't happen - approval should require a different role based on rules.

## Quick Solution

### Step 1: Disable Template Auto-Approval
```sql
-- Disable auto-approval for all templates
UPDATE ctrm.deal_templates SET auto_approval_allowed = false;
```

Or via API:
```bash
curl -X PATCH "http://localhost:8080/api/templates/1/auto-approval?enabled=false"
```

### Step 2: Create Approval Rule (SQL)
```sql
-- Rule: All trades need RISK approval
INSERT INTO ctrm.approval_rule (rule_name, trigger_event, priority, active, status, version)
VALUES ('All Trades Require Risk Approval', 'TRADE_BOOK', 1, true, 'ACTIVE', 1);

-- Condition: quantity > 0 (matches ALL trades)
INSERT INTO ctrm.approval_rule_condition (rule_id, field_code, operator, value1)
VALUES (currval('ctrm.approval_rule_rule_id_seq'), 'quantity', '>', '0');

-- Routing: RISK role must approve
INSERT INTO ctrm.approval_routing (rule_id, approval_role, approval_level)
VALUES (currval('ctrm.approval_rule_rule_id_seq'), 'RISK', 1);
```

### Step 3: Create RISK User
```sql
INSERT INTO ctrm.users (username, password_hash, email, full_name, role, active)
VALUES ('risk_user', 'EF92B778BAFE771E89245B89ECBC08A44A4E166C06659911881F383D4473E94F', 
        'risk@example.com', 'Risk Manager', 'RISK', true);
-- Password: password123
```

### Step 4: Verify Setup
```sql
-- Check rules
SELECT r.rule_name, r.active, r.trigger_event, 
       c.field_code, c.operator, c.value1,
       rt.approval_role
FROM ctrm.approval_rule r
LEFT JOIN ctrm.approval_rule_condition c ON r.rule_id = c.rule_id
LEFT JOIN ctrm.approval_routing rt ON r.rule_id = rt.rule_id
WHERE r.active = true;
```

### Step 5: Test Workflow
1. **Book trade as trader** (e.g., `pallavi_trade`)
   - Trade status: `PENDING_APPROVAL`
   - Trade `pendingApprovalRole`: `RISK`

2. **Try to approve as same trader** → Should FAIL
   - Error: "Approval requires role: RISK, but user has role: SENIOR_TRADER"

3. **Login as `risk_user` and approve** → Should SUCCEED
   - Trade status: `APPROVED`

## Alternative: Use UI

1. Login as admin
2. Go to "Approval Rules Configuration"
3. Click "+ Add Rule"
4. Fill in:
   - Rule Name: "All Trades Need Risk"
   - Trigger Event: TRADE_BOOK
   - Condition: quantity > 0
   - Routing: RISK, Level 1
5. Save as Draft → Activate

## Troubleshooting

**Issue**: Trade still auto-approved
- Check: `SELECT id, template_name, auto_approval_allowed FROM ctrm.deal_templates;`
- Fix: Ensure `auto_approval_allowed = false`

**Issue**: No rule matched
- Check: `SELECT * FROM ctrm.approval_rule WHERE active = true;`
- Fix: Create rule with condition `quantity > 0` to match all trades

**Issue**: User can still approve own trade
- Check user's role matches the required `pendingApprovalRole`
- Fix: Ensure trader has different role than approver (e.g., SENIOR_TRADER vs RISK)
