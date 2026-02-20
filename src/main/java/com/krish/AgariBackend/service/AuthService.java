package com.krish.AgariBackend.service;
import com.krish.AgariBackend.dto.LoginRequest;
import com.krish.AgariBackend.dto.LoginResponseDto;
import java.util.stream.Collectors;
import com.krish.AgariBackend.dto.RegisterRequest;
import com.krish.AgariBackend.dto.RegisterResponceDto;
import com.krish.AgariBackend.entity.Role;
import com.krish.AgariBackend.entity.User;
import com.krish.AgariBackend.repo.UserRepository;
import com.krish.AgariBackend.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(
            UserRepository userRepo,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public RegisterResponceDto register(RegisterRequest request) {

        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "Email already registered");
        }

        Set<Role> roleSet = new HashSet<>();

        if (request.getRole() != null && !request.getRole().isEmpty()) {
            for (String role : request.getRole()) {
                if (role.equalsIgnoreCase("ADMIN") || role.equalsIgnoreCase("ROLE_ADMIN")) {
                    roleSet.add(Role.ROLE_ADMIN);
                } else {
                    roleSet.add(Role.ROLE_USER);
                }
            }
        } else {
            roleSet.add(Role.ROLE_USER);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);
        user.setRoles(roleSet);

        userRepo.save(user);

        Set<String> roles = user.getRoles()
                .stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        return new RegisterResponceDto(
                "User Registered Successfully",
                user.getUsername(),
                roles);
    }

    // 🔐 LOGIN
    public LoginResponseDto login(LoginRequest request) {

        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (user.getPassword() == null) {
            throw new RuntimeException("Please login using Google");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user);

        Set<String> roles = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        return new LoginResponseDto(token, user.getUsername(), roles);
    }

}
