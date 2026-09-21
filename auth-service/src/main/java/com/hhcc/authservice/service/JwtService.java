package com.hhcc.authservice.service;

import com.hhcc.authservice.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class JwtService {

    private static final long EXPIRATION_SECONDS = 3600;

    private final JwtEncoder jwtEncoder;

    public String generateToken(User user) {
        Instant issuedAt = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(user.getId().toString())
                .issuedAt(issuedAt)
                .expiresAt(issuedAt.plusSeconds(EXPIRATION_SECONDS))
                .claim("username", user.getUsername())
                .claim("role", user.getRole())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(),
                claims)).getTokenValue();
    }

    public long getExpirationSeconds() {
        return EXPIRATION_SECONDS;
    }
}
