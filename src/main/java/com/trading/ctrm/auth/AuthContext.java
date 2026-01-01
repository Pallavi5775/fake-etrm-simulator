package com.trading.ctrm.auth;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class AuthContext {
    
    private Long userId;
    private String username;
    private String role;
    
    public void setCurrentUser(Long userId, String username, String role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getRole() {
        return role;
    }
    
    public boolean isAuthenticated() {
        return userId != null && username != null;
    }
}
