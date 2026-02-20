package com.krish.AgariBackend.security;

import com.krish.AgariBackend.entity.Role;
import com.krish.AgariBackend.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    @org.springframework.beans.factory.annotation.Value("${jwt.secret}")
    private String secret;

    private static final long EXPIRATION = 60 * 60 * 1000; // 1 hour

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // ✅ GENERATE TOKEN (Login / Register / OAuth)
    public String generateToken(User user) {

        List<String> roles = user.getRoles()
                .stream()
                .map(Role::name) // ✅ ENUM SAFE
                .toList();

        return Jwts.builder()
                .subject(user.getEmail()) // ✅ email as subject
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getKey())
                .compact();
    }

    // ✅ EXTRACT ALL CLAIMS
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // ✅ EXTRACT USERNAME / EMAIL
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // ✅ VALIDATE TOKEN
    public boolean validate(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            System.out.println("❌ JWT Validation Error: " + e.getMessage());
            return false;
        }
    }
}
