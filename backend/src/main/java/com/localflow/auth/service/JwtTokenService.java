package com.localflow.auth.service;

import com.localflow.user.domain.User;
import com.localflow.customer.domain.Customer;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class JwtTokenService {

    private static final Duration ACCESS_TOKEN_TTL = Duration.ofHours(2);

    private final JwtEncoder jwtEncoder;

    public JwtTokenService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public Token createAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(ACCESS_TOKEN_TTL);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("localflow")
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(user.getId().toString())
                .claim("userId", user.getId())
                .claim("businessId", user.getBusiness().getId())
                .claim("role", user.getRole().name())
                .claim("email", user.getEmail())
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String value = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new Token(value, ACCESS_TOKEN_TTL.toSeconds());
    }

    public Token createCustomerAccessToken(Customer customer) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(ACCESS_TOKEN_TTL);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("localflow")
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(customer.getId().toString())
                .claim("customerId", customer.getId())
                .claim("businessId", customer.getBusiness().getId())
                .claim("role", "CUSTOMER")
                .claim("email", customer.getEmail())
                .claim("name", customer.getName())
                .claim("phone", customer.getPhone())
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String value = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new Token(value, ACCESS_TOKEN_TTL.toSeconds());
    }

    public record Token(String value, long expiresInSeconds) {
    }
}

