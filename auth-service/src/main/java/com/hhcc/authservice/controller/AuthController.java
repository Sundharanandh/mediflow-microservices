package com.hhcc.authservice.controller;

import com.hhcc.authservice.dto.RegisterRequest;
import com.hhcc.authservice.dto.RegisterResponse;
import com.hhcc.authservice.model.User;
import com.hhcc.authservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {

        User user = authService.register(request);
        RegisterResponse response=RegisterResponse.builder().id(user.getId()).username(user.getUsername()).email(user.getEmail()).role(user.getRole()).active(user.getActive()).build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/health")
    public String health() {
        return "Auth Service is running";
    }

    @GetMapping("/profile")
    public String profile() {
        return "Authenticated user can access this";
    }


}