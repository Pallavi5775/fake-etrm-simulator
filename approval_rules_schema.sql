-- Approval Rule Tables

CREATE TABLE ctrm.approval_rule (
    rule_id BIGSERIAL PRIMARY KEY,
    rule_name VARCHAR(255),
    trigger_event VARCHAR(255),
    priority INTEGER,
    active BOOLEAN,
    status VARCHAR(50),
    version INTEGER,
    parent_rule_id BIGINT
);

CREATE TABLE ctrm.approval_rule_condition (
    condition_id BIGSERIAL PRIMARY KEY,
    rule_id BIGINT NOT NULL,
    field_code VARCHAR(255),
    operator VARCHAR(50),
    value1 VARCHAR(255),
    FOREIGN KEY (rule_id) REFERENCES ctrm.approval_rule(rule_id) ON DELETE CASCADE
);

CREATE TABLE ctrm.approval_routing (
    routing_id BIGSERIAL PRIMARY KEY,
    rule_id BIGINT NOT NULL,
    approval_role VARCHAR(255),
    approval_level INTEGER,
    FOREIGN KEY (rule_id) REFERENCES ctrm.approval_rule(rule_id) ON DELETE CASCADE
);

-- Sample data
INSERT INTO ctrm.approval_rule (rule_name, trigger_event, priority, active, status, version, parent_rule_id)
VALUES ('High Value Trade Approval', 'TRADE_CREATED', 1, true, 'ACTIVE', 1, NULL);

INSERT INTO ctrm.approval_rule_condition (rule_id, field_code, operator, value1)
VALUES (1, 'quantity', '>', '1000');

INSERT INTO ctrm.approval_routing (rule_id, approval_role, approval_level)
VALUES (1, 'SENIOR_TRADER', 1);
