package com.smartagriculture.smartagriculture.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * LoginRequest is the JSON a farmer sends to log in: phone number and password.
 * The messages are KEYS in curly braces, looked up in the language of the request.
 */
public class LoginRequest {

    @NotBlank(message = "{validation.phone.required}")
    private String phone;

    @NotBlank(message = "{validation.password.required}")
    private String password;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}