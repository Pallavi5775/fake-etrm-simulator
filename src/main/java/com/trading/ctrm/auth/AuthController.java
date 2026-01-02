package com.trading.ctrm.auth;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trading.ctrm.auth.dto.AuthResponse;
import com.trading.ctrm.auth.dto.LoginRequest;
import com.trading.ctrm.auth.dto.RegisterRequest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final RoleRepository roleRepository;

    public AuthController(AuthService authService, RoleRepository roleRepository) {
        this.authService = authService;
        this.roleRepository = roleRepository;
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    // Public endpoint - no authentication required (needed for registration form)
    @GetMapping("/roles")
    public List<RoleInfo> getRoles() {
        return Arrays.stream(UserRole.values())
            .map(role -> new RoleInfo(role.name(), role.getDisplayName()))
            .collect(Collectors.toList());
    }

    // Public endpoint - no authentication required
    // Get roles from database table
    @GetMapping("/roles/all")
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    // Public endpoint - no authentication required
    // Get only active roles
    @GetMapping("/roles/active")
    public List<Role> getActiveRoles() {
        return roleRepository.findByActiveTrue();
    }

    static class RoleInfo {
        private String value;
        private String label;

        public RoleInfo(String value, String label) {
            this.value = value;
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }
}
