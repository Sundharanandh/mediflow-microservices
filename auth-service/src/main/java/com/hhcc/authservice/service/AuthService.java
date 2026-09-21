package com.hhcc.authservice.service;

import com.hhcc.authservice.dto.RegisterRequest;
import com.hhcc.authservice.dto.LoginRequest;
import com.hhcc.authservice.dto.AuthResponse;
import com.hhcc.authservice.model.User;
import com.hhcc.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role("PATIENT")
                .active(true)
                .build();

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .filter(userRecord -> Boolean.TRUE.equals(userRecord.getActive()))
                .filter(userRecord -> userRecord.getPasswordHash() != null)
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        return AuthResponse.builder()
                .accessToken(jwtService.generateToken(user))
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationSeconds())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }
}