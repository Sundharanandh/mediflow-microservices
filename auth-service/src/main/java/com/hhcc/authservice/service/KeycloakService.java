package com.hhcc.authservice.service;

import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import jakarta.ws.rs.core.Response;

@Service
@RequiredArgsConstructor
public class KeycloakService {

    private final Keycloak keycloak;

    public String createUser(String username, String email, String firstName, String lastName, String password) {

        UserRepresentation user = new UserRepresentation();

        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(true);
        user.setEmailVerified(true);
        user.setRequiredActions(java.util.Collections.emptyList());

        CredentialRepresentation credential = new CredentialRepresentation();

        credential.setType(CredentialRepresentation.PASSWORD);

        credential.setValue(password);
        credential.setTemporary(false);

        user.setCredentials(java.util.List.of(credential));

        Response response = keycloak.realm("mediflow").users().create(user);

        try {

            if (response.getStatus() == 201) {

                String location = response.getHeaderString("Location");
                return location.substring(location.lastIndexOf("/") + 1);
            }
            if (response.getStatus() == 409) {throw new IllegalArgumentException("User already exists in Keycloak");
            }
            throw new RuntimeException("Failed to create Keycloak user. HTTP status: " + response.getStatus());
        } finally {
            response.close();
        }
    }


    public void deleteUser(String keycloakUserId) {
        Response response = keycloak.realm("mediflow").users().delete(keycloakUserId);
        try {
            if (response.getStatus() != 204) {
                throw new RuntimeException("Failed to delete Keycloak user. HTTP status: " + response.getStatus());
            }
        } finally {
            response.close();
        }
    }
}