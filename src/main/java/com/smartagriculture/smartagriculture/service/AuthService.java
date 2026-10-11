package com.smartagriculture.smartagriculture.service;

import com.smartagriculture.smartagriculture.dto.AuthResponse;
import com.smartagriculture.smartagriculture.dto.FarmerResponse;
import com.smartagriculture.smartagriculture.dto.LoginRequest;
import com.smartagriculture.smartagriculture.dto.RegisterRequest;
import com.smartagriculture.smartagriculture.exception.ConflictException;
import com.smartagriculture.smartagriculture.model.Farmer;
import com.smartagriculture.smartagriculture.repository.FarmerRepository;
import com.smartagriculture.smartagriculture.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.smartagriculture.smartagriculture.exception.ApiException;

import java.nio.charset.StandardCharsets;

/**
 * AuthService contains the business logic for registering and logging in farmers.
 */
// @Service tells Spring this class contains business logic.
@Service
public class AuthService {

    // BCrypt only uses the first 72 BYTES of a password.
    private static final int MAX_PASSWORD_BYTES = 72;

    private final FarmerRepository farmerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // Constructor injection: Spring provides these objects.
    public AuthService(FarmerRepository farmerRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.farmerRepository = farmerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // Create a new farmer account and log them in straight away.
    public AuthResponse register(RegisterRequest request) {
        // Characters like Telugu letters use more than 1 byte, so we check bytes, not characters.
        if (isTooLong(request.getPassword())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "auth.passwordTooLong");
        }

        // The phone number is the login name, so it must be unique.
        if (farmerRepository.existsByPhone(request.getPhone())) {
            throw new ConflictException("auth.phoneTaken");
        }

        Farmer farmer = new Farmer();
        farmer.setName(request.getName().trim());
        farmer.setPhone(request.getPhone());
        // An empty email is saved as "no email" (null).
        farmer.setEmail(request.getEmail() == null || request.getEmail().isBlank() ? null : request.getEmail().trim());
        // We save the HASH, never the real password.
        farmer.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        Farmer saved = farmerRepository.save(farmer);

        return new AuthResponse(jwtService.generateToken(saved), FarmerResponse.from(saved));
    }

    // Check phone + password. If they are correct, return a token.
    public AuthResponse login(LoginRequest request) {
        Farmer farmer = farmerRepository.findByPhone(request.getPhone()).orElse(null);

        // The password is correct only if: the farmer exists, has a password,
        // and the typed password matches the saved hash.
        boolean valid = farmer != null
                && farmer.getPasswordHash() != null
                && !isTooLong(request.getPassword())
                && passwordEncoder.matches(request.getPassword(), farmer.getPasswordHash());

        if (!valid) {
            // The SAME message for "unknown phone" and "wrong password",
            // so nobody can use the login form to find out which phone numbers are registered.
            throw new ApiException(HttpStatus.UNAUTHORIZED, "auth.invalidCredentials");
        }

        return new AuthResponse(jwtService.generateToken(farmer), FarmerResponse.from(farmer));
    }

    // Helper: true if the password is longer than BCrypt's 72-byte limit.
    private boolean isTooLong(String password) {
        return password.getBytes(StandardCharsets.UTF_8).length > MAX_PASSWORD_BYTES;
    }
}