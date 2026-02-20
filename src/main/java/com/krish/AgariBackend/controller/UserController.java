package com.krish.AgariBackend.controller;

import com.krish.AgariBackend.entity.User;
import com.krish.AgariBackend.repo.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 🔐 ADMIN: Get All Users
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // 🔐 ADMIN: Get User By ID
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(java.util.Objects.requireNonNull(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔐 ADMIN: Delete User
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (id == null || !userRepository.existsById(java.util.Objects.requireNonNull(id))) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(java.util.Objects.requireNonNull(id));
        return ResponseEntity.noContent().build();
    }

    // 👤 Get Current User Profile
    @GetMapping("/profile")
    public ResponseEntity<com.krish.AgariBackend.dto.UserProfileDto> getMyProfile(
            org.springframework.security.core.Authentication authentication) {
        System.out.println("👤 UserController: Hit /api/users/profile for user: " + authentication.getName());
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .map(user -> new com.krish.AgariBackend.dto.UserProfileDto(
                        user.getDisplayName(), // Display Name
                        user.getEmail(),
                        user.getMobile(),
                        user.getAddress()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 👤 Update Current User Profile
    @PutMapping("/profile")
    public ResponseEntity<com.krish.AgariBackend.dto.UserProfileDto> updateMyProfile(
            org.springframework.security.core.Authentication authentication,
            @RequestBody com.krish.AgariBackend.dto.UserProfileDto dto) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .map(user -> {
                    if (dto.getUsername() != null) {
                        user.setUsername(dto.getUsername()); // Updates display name
                    }
                    if (dto.getMobile() != null) {
                        user.setMobile(dto.getMobile());
                    }
                    if (dto.getAddress() != null) {
                        user.setAddress(dto.getAddress());
                    }

                    User saved = userRepository.save(user);

                    return new com.krish.AgariBackend.dto.UserProfileDto(
                            saved.getDisplayName(),
                            saved.getEmail(),
                            saved.getMobile(),
                            saved.getAddress());
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
