package com.smartagriculture.smartagriculture.controller;

import com.smartagriculture.smartagriculture.dto.AuthResponse;
import com.smartagriculture.smartagriculture.dto.LoginRequest;
import com.smartagriculture.smartagriculture.dto.RegisterRequest;
import com.smartagriculture.smartagriculture.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * AuthController handles register and login requests.
 * These two URLs will stay OPEN (no token needed) when we lock the rest in Step 19,
 * because a farmer has no token before logging in.
 */
// @RestController: handles REST requests and returns JSON.
@RestController
// @RequestMapping: all URLs here start with /api/auth.
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // POST /api/auth/register -> create an account (201 Created) and return a token.
    // @Valid runs the checks from RegisterRequest (phone format, password length...).
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    // POST /api/auth/login -> check phone + password and return a token.
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}