package com.hhcc.authservice.service;

import com.hhcc.authservice.dto.RegisterRequest;
import com.hhcc.authservice.model.User;
import com.hhcc.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final KeycloakService keycloakService;

    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        String keycloakUserId = null;

        try {
            /*
             * Step 1:
             * Create user in Keycloak
             */
            keycloakUserId = keycloakService.createUser(
                    request.getUsername(),
                    request.getEmail(),
                    request.getFirstName(),
                    request.getLastName(),
                    request.getPassword()
            );

            /*
             * Step 2:
             * Create MediFlow application user
             */
            User user = User.builder()
                    .keycloakUserId(keycloakUserId)
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .role("PATIENT")
                    .active(true)
                    .build();

            /*
             * Step 3:
             * Save user in Oracle DB
             */
            return userRepository.save(user);

        } catch (Exception exception) {
            /*
             * Compensation:
             * If DB save fails after Keycloak user creation,
             * remove the Keycloak user.
             */
            if (keycloakUserId != null) {
                try {
                    keycloakService.deleteUser(keycloakUserId);
                } catch (Exception deleteException) {
                    // Log this later using centralized error logging
                }
            }
            throw exception;
        }
    }
}