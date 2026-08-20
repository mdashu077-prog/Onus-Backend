package com.onus.backend.controller;

import com.onus.backend.dto.LoginRequest;
import com.onus.backend.dto.RegisterRequest;
import com.onus.backend.entity.User;
import com.onus.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request) {

        User user = authService.register(request);

        // Password response mein nahi bhejna
        user.setPasswordHash(null);

        return ResponseEntity
                .status(201)
                .body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request) {

        String token = authService.login(request);

        return ResponseEntity.ok(
                Map.of(
                        "token", token
                )
        );
    }
}