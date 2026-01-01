-- Multi-Level Approval Test Script
-- This script demonstrates setting up and testing a 3-level approval workflow

-- ==============================================
-- SETUP: Create users with different roles
-- ==============================================

-- Trader who creates trades
INSERT INTO ctrm.users (username, password_hash, role, created_at)
VALUES ('pallavi_trade', 'hashed_password_here', 'TRADER', NOW())
ON CONFLICT (username) DO NOTHING;

-- Level 1 Approver: Senior Trader
INSERT INTO ctrm.users (username, password_hash, role, created_at)
VALUES ('john_senior', 'hashed_password_here', 'SENIOR_TRADER', NOW())
ON CONFLICT (username) DO NOTHING;

-- Level 2 Approver: Risk Manager
INSERT INTO ctrm.users (username, password_hash, role, created_at)
VALUES ('sarah_risk', 'hashed_password_here', 'RISK', NOW())
ON CONFLICT (username) DO NOTHING;

-- Level 3 Approver: Head Trader
INSERT INTO ctrm.users (username, password_hash, role, created_at)
VALUES ('mike_head', 'hashed_password_here', 'HEAD_TRADER', NOW())
ON CONFLICT (username) DO NOTHING;

-- ==============================================
-- STEP 1: Create Approval Rule with 3 Levels
-- ==============================================

-- Create the rule
INSERT INTO ctrm.approval_rule (rule_name, trigger_event, priority, active, status, version)
VALUES ('High Value Multi-Level', 'TRADE_BOOK', 1, true, 'ACTIVE', 1)
RETURNING rule_id;
-- Let's say this returns rule_id = 100

-- Add condition: quantity > 500000
INSERT INTO ctrm.approval_rule_condition (rule_id, field_code, operator, value1)
VALUES (100, 'QUANTITY', '>', '500000');

-- Define 3-level routing: SENIOR_TRADER → RISK → HEAD_TRADER
INSERT INTO ctrm.approval_routing (rule_id, approval_role, approval_level)
VALUES
    (100, 'SENIOR_TRADER', 1),
    (100, 'RISK', 2),
    (100, 'HEAD_TRADER', 3);

-- ==============================================
-- STEP 2: Verify the rule setup
-- ==============================================

SELECT 
    ar.rule_id,
    ar.rule_name,
    ar.trigger_event,
    ar.active,
    aro.approval_level,
    aro.approval_role
FROM ctrm.approval_rule ar
JOIN ctrm.approval_routing aro ON ar.rule_id = aro.rule_id
WHERE ar.rule_id = 100
ORDER BY aro.approval_level;

-- Expected output:
-- rule_id | rule_name                 | trigger_event | active | approval_level | approval_role
-- --------+---------------------------+---------------+--------+----------------+---------------
-- 100     | High Value Multi-Level    | TRADE_BOOK    | t      | 1              | SENIOR_TRADER
-- 100     | High Value Multi-Level    | TRADE_BOOK    | t      | 2              | RISK
-- 100     | High Value Multi-Level    | TRADE_BOOK    | t      | 3              | HEAD_TRADER

-- ==============================================
-- STEP 3: Create a deal template for testing
-- ==============================================

-- Create test instrument if needed
INSERT INTO ctrm.instrument (instrument_code, instrument_type, underlying_commodity)
VALUES ('PWR-FWD-TEST', 'POWER_FORWARD', 'POWER')
ON CONFLICT (instrument_code) DO NOTHING;

-- Create template with auto-approval disabled
INSERT INTO ctrm.deal_template (
    template_name,
    instrument_id,
    default_quantity,
    default_price,
    auto_approval_allowed,
    created_at
)
SELECT 
    'High Value Test Template',
    i.id,
    1000000.00,
    50.00,
    false,  -- Auto-approval OFF
    NOW()
FROM ctrm.instrument i
WHERE i.instrument_code = 'PWR-FWD-TEST'
ON CONFLICT DO NOTHING;

-- ==============================================
-- TEST SCENARIO
-- ==============================================

-- Now use the UI or API to:

-- 1. Book a trade (as pallavi_trade):
--    POST /api/templates/{templateId}/book
--    {
--      "quantity": 800000,
--      "buySell": "BUY",
--      "counterparty": "SHELL",
--      "portfolio": "POWER_TRADING"
--    }
--
--    Expected result:
--    - status: PENDING_APPROVAL
--    - currentApprovalLevel: 1
--    - pendingApprovalRole: SENIOR_TRADER
--    - matchedRuleId: 100

-- 2. Check trade state:
--    SELECT trade_id, status, current_approval_level, pending_approval_role, matched_rule_id
--    FROM ctrm.trade
--    WHERE trade_id = '<your-trade-id>';

-- 3. Approve as john_senior (SENIOR_TRADER):
--    POST /api/trades/{tradeId}/approve
--    Headers: X-User-Role: SENIOR_TRADER, X-User-Name: john_senior
--
--    Expected result:
--    - status: PENDING_APPROVAL (still pending!)
--    - currentApprovalLevel: 2 (advanced to level 2)
--    - pendingApprovalRole: RISK

-- 4. Approve as sarah_risk (RISK):
--    POST /api/trades/{tradeId}/approve
--    Headers: X-User-Role: RISK, X-User-Name: sarah_risk
--
--    Expected result:
--    - status: PENDING_APPROVAL (still pending!)
--    - currentApprovalLevel: 3 (advanced to level 3)
--    - pendingApprovalRole: HEAD_TRADER

-- 5. Approve as mike_head (HEAD_TRADER):
--    POST /api/trades/{tradeId}/approve
--    Headers: X-User-Role: HEAD_TRADER, X-User-Name: mike_head
--
--    Expected result:
--    - status: APPROVED (finally approved!)
--    - currentApprovalLevel: NULL
--    - pendingApprovalRole: NULL

-- ==============================================
-- VERIFICATION QUERIES
-- ==============================================

-- View all trades with approval status
SELECT 
    t.trade_id,
    t.status,
    t.quantity,
    t.current_approval_level,
    t.pending_approval_role,
    t.matched_rule_id,
    ar.rule_name
FROM ctrm.trade t
LEFT JOIN ctrm.approval_rule ar ON t.matched_rule_id = ar.rule_id
WHERE t.status IN ('PENDING_APPROVAL', 'APPROVED')
ORDER BY t.created_at DESC
LIMIT 10;

-- View approval routing for a specific rule
SELECT 
    approval_level,
    approval_role
FROM ctrm.approval_routing
WHERE rule_id = 100
ORDER BY approval_level;

-- View approval history from events
SELECT 
    te.trade_id,
    te.event_type,
    te.triggered_by,
    te.event_time
FROM ctrm.trade_event te
WHERE te.event_type = 'APPROVED'
ORDER BY te.event_time DESC
LIMIT 20;

-- ==============================================
-- ERROR SCENARIO TESTS
-- ==============================================

-- Test 1: Wrong role tries to approve
-- If RISK user tries to approve when level=1 (requires SENIOR_TRADER)
-- Expected: 403 Forbidden with message "Approval requires role: SENIOR_TRADER, but user has role: RISK"

-- Test 2: Same user cannot create and approve
-- If pallavi_trade (TRADER) tries to approve their own trade
-- Expected: 403 Forbidden with message "Approval requires role: SENIOR_TRADER, but user has role: TRADER"

-- Test 3: Skipping approval levels
-- Cannot jump from level 1 to level 3
-- System enforces sequential progression

-- ==============================================
-- CLEANUP (if needed)
-- ==============================================

-- Delete test data (CAREFUL - only in dev environment!)
-- DELETE FROM ctrm.trade_event WHERE trade_id IN (SELECT trade_id FROM ctrm.trade WHERE matched_rule_id = 100);
-- DELETE FROM ctrm.trade WHERE matched_rule_id = 100;
-- DELETE FROM ctrm.approval_routing WHERE rule_id = 100;
-- DELETE FROM ctrm.approval_rule_condition WHERE rule_id = 100;
-- DELETE FROM ctrm.approval_rule WHERE rule_id = 100;
