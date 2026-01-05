# CTRM System Roles

This document describes the role-based access control (RBAC) system for the Commodity Trading and Risk Management (CTRM) simulator.

## Role Categories

### **System Administration**
- **ADMIN**: Complete system access including user management, configuration, and audit logs

### **Trading Operations**
- **SENIOR_TRADER**: Execute complex trades, manage large positions, and lead trading strategies
- **HEAD_TRADER**: Oversee all trading activities, manage trader teams, and set trading policies

### **Risk Management**
- **RISK**: Risk oversight, limit setting, risk reporting, and risk mitigation

### **Compliance & Audit**
- **COMPLIANCE**: Regulatory compliance, trade review, compliance reporting

### **Executive**
- **CFO**: Chief Financial Officer with access to financial reports, treasury management, and executive oversight

## Role Permissions Matrix

| Permission | ADMIN | SENIOR_TRADER | HEAD_TRADER | RISK | COMPLIANCE | CFO |
|------------|-------|---------------|-------------|------|------------|-----|
| Create Trades | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ |
| View Trades | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| Modify Trades | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ |
| Approve Trades | ✅ | ❌ | ✅ | ✅ | ✅ | ✅ |
| View Positions | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| Risk Reports | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| Market Data | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| User Management | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |
| System Config | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |
| Financial Reports | ✅ | ❌ | ❌ | ✅ | ❌ | ✅ |

## Implementation Notes

- Roles are stored in the `ctrm.roles` table
- Users are assigned roles through the `user_roles` junction table
- Permissions are enforced at the application level using Spring Security
- Role-based access control ensures segregation of duties
- Audit logging captures all role-based access decisions

## Usage

```sql
-- Assign role to user
INSERT INTO ctrm.user_roles (user_id, role_id)
SELECT u.user_id, r.role_id
FROM ctrm.users u, ctrm.roles r
WHERE u.username = 'john.doe'
AND r.role_name = 'TRADER';
```

## Security Considerations

- **Principle of Least Privilege**: Users get minimum required permissions
- **Segregation of Duties**: Trading and approval functions are separated
- **Audit Trail**: All role assignments and permission changes are logged
- **Regular Review**: Role assignments should be reviewed periodically