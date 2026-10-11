package com.smartagriculture.smartagriculture.security;

import com.smartagriculture.smartagriculture.model.Farmer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * JwtService creates login tokens (JWT) and checks them.
 * The token proves "this request comes from farmer N", and is signed with our secret key.
 */
// @Service tells Spring this class contains logic.
@Service
public class JwtService {

    // The secret key used to sign and check tokens.
    private final SecretKey key;

    // How many minutes a token stays valid.
    private final long expirationMinutes;

    // @Value reads a setting from application.properties (which reads the environment variable).
    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration-minutes}") long expirationMinutes) {
        // A short secret is easy to guess, so we refuse to start with one.
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("JWT_SECRET must be at least 32 characters long");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMinutes = expirationMinutes;
    }

    // Creates a signed token for this farmer.
    public String generateToken(Farmer farmer) {
        Instant now = Instant.now();

        return Jwts.builder()
                // "subject" = who the token is for. We store ONLY the farmer id.
                .subject(String.valueOf(farmer.getId()))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expirationMinutes, ChronoUnit.MINUTES)))
                // Sign it with our secret key. Changing anything inside breaks the signature.
                .signWith(key)
                .compact();
    }

    // NEW: Checks a token and returns the farmer id inside it.
    // Returns null if the token is fake, changed, expired, or not a token at all.
    public Long extractFarmerId(String token) {
        try {
            // verifyWith(key): checks the signature. parseSignedClaims also checks the expiry time.
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Long.valueOf(claims.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            // Any problem means "not a valid token". We do not say why.
            return null;
        }
    }
}