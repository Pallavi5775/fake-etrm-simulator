package com.trading.ctrm.auth;

import org.springframework.stereotype.Service;

import com.trading.ctrm.auth.dto.AuthResponse;
import com.trading.ctrm.auth.dto.LoginRequest;
import com.trading.ctrm.auth.dto.RegisterRequest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AuthResponse register(RegisterRequest request) {
        // Validate username
        if (userRepository.existsByUsername(request.getUsername())) {
            return new AuthResponse("Username already exists");
        }

        // Validate email
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            return new AuthResponse("Email already exists");
        }

        // Create user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(hashPassword(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setRole(request.getRole());
        user.setActive(true);

        User saved = userRepository.save(user);

        // Generate token
        String token = generateToken(saved);

        return new AuthResponse(
            saved.getUserId(),
            saved.getUsername(),
            saved.getEmail(),
            saved.getFullName(),
            saved.getRole(),
            token
        );
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
            .orElse(null);

        if (user == null) {
            return new AuthResponse("Invalid username or password");
        }

        if (!user.getActive()) {
            return new AuthResponse("Account is disabled");
        }

        // Verify password
        String hashedPassword = hashPassword(request.getPassword());
        if (!hashedPassword.equals(user.getPasswordHash())) {
            return new AuthResponse("Invalid username or password");
        }

        // Generate token
        String token = generateToken(user);

        return new AuthResponse(
            user.getUserId(),
            user.getUsername(),
            user.getEmail(),
            user.getFullName(),
            user.getRole(),
            token
        );
    }

    private String hashPassword(String password) {
        // For simulator/demo purposes, store plain text passwords
        return password;
    }

    private String generateToken(User user) {
        // Simple token generation - in production, use JWT
        String tokenData = user.getUserId() + ":" + user.getUsername() + ":" + UUID.randomUUID();
        return Base64.getEncoder().encodeToString(tokenData.getBytes(StandardCharsets.UTF_8));
    }
}
