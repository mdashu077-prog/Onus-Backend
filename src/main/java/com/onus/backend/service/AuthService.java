package com.onus.backend.service;

import com.onus.backend.dto.LoginRequest;
import com.onus.backend.dto.RegisterRequest;
import com.onus.backend.entity.User;
import com.onus.backend.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepo,
            BCryptPasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public User register(RegisterRequest req) {

        userRepo.findByEmail(req.email.toLowerCase()).ifPresent(u -> {
            throw new RuntimeException("Email already registered");
        });

        User user = new User();

        user.setName(req.name);
        user.setEmail(req.email.toLowerCase());
        user.setRole(req.role);

        // Password ko BCrypt se encrypt/hash karna
        user.setPasswordHash(
                passwordEncoder.encode(req.password)
        );

        return userRepo.save(user);
    }

    public String login(LoginRequest req) {

        User user = userRepo.findByEmail(req.email.toLowerCase())
                .orElseThrow(() ->
                        new RuntimeException("Invalid credentials"));

        // Role check
        if (!user.getRole().equals(req.role)) {
            throw new RuntimeException(
                    "Please login as " + user.getRole()
            );
        }

        // Password check
        if (!passwordEncoder.matches(
                req.password,
                user.getPasswordHash())) {

            throw new RuntimeException("Invalid credentials");
        }

        // JWT token generate
        return jwtService.generateToken(
                user.getEmail(),
                user.getRole()
        );
    }
}