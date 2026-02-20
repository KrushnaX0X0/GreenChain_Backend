package com.krish.AgariBackend.service;

import com.krish.AgariBackend.entity.User;
import com.krish.AgariBackend.repo.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public Map<String, Object> getUserProfile(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> profile = new HashMap<>();
        profile.put("username", user.getUsername()); // This acts as display name often
        // profile.put("displayName", user.getDisplayName()); // if distinct
        profile.put("email", user.getEmail());
        profile.put("mobile", user.getMobile());
        profile.put("address", user.getAddress());
        profile.put("roles", user.getRoles());
        return profile;
    }

    public void updateUserProfile(String email, Map<String, String> updates) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (updates.containsKey("username")) {
            user.setUsername(updates.get("username"));
        }
        if (updates.containsKey("mobile")) {
            user.setMobile(updates.get("mobile"));
        }
        if (updates.containsKey("address")) {
            user.setAddress(updates.get("address"));
        }

        userRepo.save(java.util.Objects.requireNonNull(user));
    }
}
