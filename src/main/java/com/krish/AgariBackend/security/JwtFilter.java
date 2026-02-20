package com.krish.AgariBackend.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        System.out.println("🛡️ JwtFilter: Intercepting " + request.getMethod() + " " + request.getRequestURI());

        if (header != null) {
            System.out.println("   Header found: " + (header.length() > 20 ? header.substring(0, 20) + "..." : header));
        } else {
            System.out.println("   No Authorization Header");
        }

        if (header != null && header.startsWith("Bearer ")) {

            String token = header.substring(7);
            System.out.println(
                    "🔍 JwtFilter: Extracted Token: " + (token.length() > 15 ? token.substring(0, 15) + "..." : token));

            if (jwtUtil.validate(token)) {
                System.out.println("✅ JwtFilter: Token Validated Successfully");

                Claims claims = jwtUtil.extractAllClaims(token);

                String username = claims.getSubject();
                @SuppressWarnings("unchecked")
                List<String> roles = claims.get("roles", List.class);

                System.out.println("👤 JwtFilter: User Subject: " + username);
                System.out.println("🛡️ JwtFilter: Roles in Token: " + roles);

                if (roles == null) {
                    roles = List.of();
                }

                var authorities = roles.stream()
                        .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                        .map(SimpleGrantedAuthority::new)
                        .toList();

                System.out.println("🔐 JwtFilter: Final Authorities: " + authorities);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        authorities);

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
            } else {
                System.err.println("❌ JwtFilter: Token Validation FAILED");
            }
        } else {
            System.out.println("⚠️ JwtFilter: No Bearer Token found in header");
        }

        filterChain.doFilter(request, response);
    }
}
