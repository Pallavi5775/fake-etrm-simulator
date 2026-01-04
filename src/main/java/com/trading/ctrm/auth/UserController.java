package com.trading.ctrm.auth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
            .map(UserDto::from)
            .collect(Collectors.toList());
    }

    public static class UserDto {
        private Long userId;
        private String username;
        private String password; // Plain text for demo
        private String email;
        private String fullName;
        private UserRole role;
        private Boolean active;

        public static UserDto from(User user) {
            UserDto dto = new UserDto();
            dto.userId = user.getUserId();
            dto.username = user.getUsername();
            dto.password = user.getPasswordHash(); // Plain text
            dto.email = user.getEmail();
            dto.fullName = user.getFullName();
            dto.role = user.getRole();
            dto.active = user.getActive();
            return dto;
        }

        // Getters
        public Long getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public String getEmail() { return email; }
        public String getFullName() { return fullName; }
        public UserRole getRole() { return role; }
        public Boolean getActive() { return active; }
    }
}