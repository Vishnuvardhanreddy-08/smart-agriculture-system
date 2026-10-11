package com.smartagriculture.smartagriculture.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Farmer represents one farmer in our system.
 * Each object of this class = one row in the "farmers" table.
 */
// @Entity tells JPA that this class represents a database table.
@Entity
// @Table sets the table name in the database.
@Table(name = "farmers")
public class Farmer {

    // @Id marks this field as the primary key (unique farmer number).
    @Id
    // @GeneratedValue lets the database create the ID automatically (1, 2, 3...).
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @NotBlank: validation, name cannot be null, empty or only spaces.
    @NotBlank(message = "Name is required")
    // @Column(nullable = false): the database column cannot be empty either.
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Phone is required")
    // @Pattern checks the format: 10 digits starting with 6, 7, 8 or 9 (Indian mobile).
    @Pattern(regexp = "^[6-9][0-9]{9}$", message = "Phone must be a valid 10-digit mobile number")
    // NEW: unique = true means no two farmers can have the same phone number.
    // The phone number is the farmer's login name.
    @Column(nullable = false, length = 10, unique = true)
    private String phone;

    // @Email checks that the text looks like an email. Email is optional here.
    @Email(message = "Email format is not valid")
    private String email;

    // NEW: the BCrypt HASH of the password (never the password itself).
    // @JsonIgnore hides it from JSON completely: it is never sent out, and never read in.
    // It is nullable because farmers created before Step 18 have no password yet.
    @JsonIgnore
    @Column(name = "password_hash", length = 100)
    private String passwordHash;

    // JPA REQUIRES a no-argument constructor. It uses it to create objects from database rows.
    public Farmer() {
    }

    // Getters and setters: Spring uses them to read and write the fields.
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}