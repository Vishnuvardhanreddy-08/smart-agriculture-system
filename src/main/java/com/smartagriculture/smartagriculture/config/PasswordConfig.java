package com.smartagriculture.smartagriculture.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * PasswordConfig creates the tool that hashes and checks passwords.
 */
// @Configuration tells Spring this class contains settings and "beans" (shared objects).
@Configuration
public class PasswordConfig {

    // @Bean means: Spring creates this object once and gives it to any class that asks for it.
    // PasswordEncoder is the general type. BCryptPasswordEncoder is the BCrypt implementation.
    //   passwordEncoder.encode("Farm@1234")      -> "$2a$10$..." (hash with random salt)
    //   passwordEncoder.matches("Farm@1234", h)  -> true or false
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}