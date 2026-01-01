package com.trading.ctrm.auth.dto;

import com.trading.ctrm.auth.UserRole;

public class AuthResponse {
    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private UserRole role;
    private String token;
    private String message;

    public AuthResponse(String message) {
        this.message = message;
    }

    public AuthResponse(Long userId, String username, String email, String fullName, UserRole role, String token) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.token = token;
        this.message = "Success";
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
