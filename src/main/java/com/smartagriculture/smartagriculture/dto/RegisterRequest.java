package com.smartagriculture.smartagriculture.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * RegisterRequest is the JSON a new farmer sends to create an account.
 * It accepts ONLY these four fields (a client cannot sneak in an id or anything else).
 * The messages are KEYS in curly braces, looked up in the language of the request.
 */
public class RegisterRequest {

    @NotBlank(message = "{validation.name.required}")
    private String name;

    @NotBlank(message = "{validation.phone.required}")
    @Pattern(regexp = "^[6-9][0-9]{9}$", message = "{validation.phone.format}")
    private String phone;

    // Optional.
    @Email(message = "{validation.email.format}")
    private String email;

    // BCrypt can only use the first 72 bytes of a password, so we limit the length.
    // In the message file, {min} and {max} are replaced by 8 and 72 from this annotation.
    @NotBlank(message = "{validation.password.required}")
    @Size(min = 8, max = 72, message = "{validation.password.size}")
    private String password;

    // Getters and setters (Spring uses them to fill this object from the JSON).
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}