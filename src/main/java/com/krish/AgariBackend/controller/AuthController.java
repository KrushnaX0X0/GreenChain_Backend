package com.krish.AgariBackend.controller;

// import com.krish.AgariBackend.dto.AuthResponse;
import com.krish.AgariBackend.dto.LoginRequest;
import com.krish.AgariBackend.dto.LoginResponseDto;
import com.krish.AgariBackend.dto.RegisterRequest;
import com.krish.AgariBackend.dto.RegisterResponceDto;
import com.krish.AgariBackend.service.AuthService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 📝 REGISTER
    @PostMapping("/register")
    public ResponseEntity<RegisterResponceDto> register(
            @RequestBody RegisterRequest request) {

        RegisterResponceDto response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    // 🔐 LOGIN
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @RequestBody LoginRequest request) {

        LoginResponseDto response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    // 🔄 Google Redirect Failsafe
    @GetMapping("/google")
    public void googleLogin(jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
        response.sendRedirect("/oauth2/authorization/google");
    }

    @GetMapping("/health")
    public String health(){
        return "server Runnning Successfully..!"; 
    }

}
