package com.smartagriculture.smartagriculture.dto;

/**
 * AuthResponse is what the server sends back after a successful register or login:
 * the login token, and the farmer's safe details.
 */
public class AuthResponse {

    private final String token;
    // "Bearer" is the standard word used when sending a token: Authorization: Bearer <token>
    private final String tokenType = "Bearer";
    private final FarmerResponse farmer;

    public AuthResponse(String token, FarmerResponse farmer) {
        this.token = token;
        this.farmer = farmer;
    }

    // Only getters are needed to create the JSON.
    public String getToken() {
        return token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public FarmerResponse getFarmer() {
        return farmer;
    }
}