-- Approval Task table for tracking approval workflow
CREATE TABLE ctrm.approval_task (
    task_id BIGSERIAL PRIMARY KEY,
    trade_id VARCHAR(255) NOT NULL,
    rule_id BIGINT NOT NULL,
    trigger_event VARCHAR(50),
    status VARCHAR(50) NOT NULL,
    current_approval_role VARCHAR(50),
    current_approval_level INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    FOREIGN KEY (rule_id) REFERENCES ctrm.approval_rule(rule_id)
);

-- Index for faster lookups
CREATE INDEX idx_approval_task_trade_id ON ctrm.approval_task(trade_id);
CREATE INDEX idx_approval_task_status ON ctrm.approval_task(status);

-- Sample approval rule: High value trades require RISK approval
INSERT INTO ctrm.approval_rule (rule_name, trigger_event, priority, active, status, version, parent_rule_id)
VALUES ('High Value Trade Approval', 'TRADE_BOOK', 1, true, 'ACTIVE', 1, NULL);

-- Condition: quantity > 1000
INSERT INTO ctrm.approval_rule_condition (rule_id, field_code, operator, value1)
VALUES (currval('ctrm.approval_rule_rule_id_seq'), 'quantity', '>', '1000');

-- Routing: First approval from RISK manager
INSERT INTO ctrm.approval_routing (rule_id, approval_role, approval_level)
VALUES (currval('ctrm.approval_rule_rule_id_seq'), 'RISK', 1);

-- Sample approval rule: Very high value trades require multiple approvals
INSERT INTO ctrm.approval_rule (rule_name, trigger_event, priority, active, status, version, parent_rule_id)
VALUES ('Very High Value Multi-Level Approval', 'TRADE_BOOK', 2, true, 'ACTIVE', 1, NULL);

-- Condition: quantity > 10000
INSERT INTO ctrm.approval_rule_condition (rule_id, field_code, operator, value1)
VALUES (currval('ctrm.approval_rule_rule_id_seq'), 'quantity', '>', '10000');

-- Routing: Level 1 - SENIOR_TRADER
INSERT INTO ctrm.approval_routing (rule_id, approval_role, approval_level)
VALUES (currval('ctrm.approval_rule_rule_id_seq'), 'SENIOR_TRADER', 1);

-- Routing: Level 2 - HEAD_TRADER
INSERT INTO ctrm.approval_routing (rule_id, approval_role, approval_level)
VALUES (currval('ctrm.approval_rule_rule_id_seq'), 'HEAD_TRADER', 2);

-- Routing: Level 3 - CFO
INSERT INTO ctrm.approval_routing (rule_id, approval_role, approval_level)
VALUES (currval('ctrm.approval_rule_rule_id_seq'), 'CFO', 3);

-- Sample approval rule: Specific counterparty requires COMPLIANCE approval
INSERT INTO ctrm.approval_rule (rule_name, trigger_event, priority, active, status, version, parent_rule_id)
VALUES ('High Risk Counterparty Check', 'TRADE_BOOK', 3, true, 'ACTIVE', 1, NULL);

-- Condition: counterparty == 'HIGH_RISK_COUNTERPARTY'
INSERT INTO ctrm.approval_rule_condition (rule_id, field_code, operator, value1)
VALUES (currval('ctrm.approval_rule_rule_id_seq'), 'counterparty', '==', 'HIGH_RISK_COUNTERPARTY');

-- Routing: COMPLIANCE approval required
INSERT INTO ctrm.approval_routing (rule_id, approval_role, approval_level)
VALUES (currval('ctrm.approval_rule_rule_id_seq'), 'COMPLIANCE', 1);
