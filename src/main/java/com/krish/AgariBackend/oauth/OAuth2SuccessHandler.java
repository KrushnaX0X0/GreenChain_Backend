package com.krish.AgariBackend.oauth;

import com.krish.AgariBackend.entity.Role;
import com.krish.AgariBackend.entity.User;
import com.krish.AgariBackend.repo.UserRepository;
import com.krish.AgariBackend.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;

    public OAuth2SuccessHandler(UserRepository userRepo, JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String emailRes = oAuth2User.getAttribute("email");
        if (emailRes == null) {
            emailRes = oAuth2User.getAttribute("sub");
        }
        if (emailRes == null) {
            emailRes = oAuth2User.getName(); // Fallback to principal name
        }
        final String email = emailRes;

        User user = userRepo.findByEmail(email)
                .orElseGet(() -> {
                    User u = new User();
                    String name = oAuth2User.getAttribute("name");
                    // Safe logic for username
                    if (name != null) {
                        u.setUsername(name);
                    } else if (email != null && email.contains("@")) {
                        u.setUsername(email.split("@")[0]);
                    } else {
                        u.setUsername(email);
                    }

                    u.setEmail(email);
                    u.setPassword(UUID.randomUUID().toString());
                    u.setEnabled(true);
                    u.setRoles(new HashSet<>());

                    // Force commit the user to get ID for Foreign Key
                    User savedUser = userRepo.saveAndFlush(u);
                    savedUser.setRoles(Set.of(Role.ROLE_USER));
                    return userRepo.saveAndFlush(savedUser);
                });

        String token = jwtUtil.generateToken(user);

        // Standard Redirect to your React route
        String targetUrl = "http://localhost:5173/oauth-success?token=" + token;
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}