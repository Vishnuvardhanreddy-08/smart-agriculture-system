package com.smartagriculture.smartagriculture.config;

import com.smartagriculture.smartagriculture.security.JwtAuthenticationFilter;
import com.smartagriculture.smartagriculture.security.JwtService;
import com.smartagriculture.smartagriculture.security.RestSecurityErrorHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * SecurityConfig decides which URLs are open and which are locked.
 * Open:   /api/auth/**  (register and login: a farmer has no token yet)
 * Locked: everything else (a valid token is needed)
 */
// @Configuration: this class contains settings and beans.
// @EnableWebSecurity: turns on Spring Security's web protection.
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtService jwtService;

    public SecurityConfig(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    // The "filter chain": the line of checkpoints every request passes through.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   RestSecurityErrorHandler errorHandler) throws Exception {
        http
                // CSRF protection is for cookie-based logins. We use a token in a header, so it is off.
                .csrf(AbstractHttpConfigurer::disable)

                // Use the CORS rules from corsConfigurationSource() below.
                .cors(Customizer.withDefaults())

                // STATELESS = the server keeps no login session. The token is sent every time.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Turn off the built-in login page and browser login box. We use our own login API.
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)

                // The rules: which URLs are open, which are locked.
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // browser "preflight" checks
                        .requestMatchers("/api/auth/**").permitAll()             // register and login
                        .anyRequest().authenticated())                           // everything else needs a token

                // What to send back when someone is refused (our usual JSON shape).
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(errorHandler)
                        .accessDeniedHandler(errorHandler))

                // Put our token-reading filter in the chain.
                // NOTE: we create it with "new" here and do NOT mark it @Component.
                // A filter that is also a bean would run twice.
                .addFilterBefore(new JwtAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // CORS rules (moved here from the old CorsConfig). Only our own frontend address is allowed.
    // The method name "corsConfigurationSource" is what Spring Security looks for.
    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origins}") String[] allowedOrigins) {

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(allowedOrigins));   // never "*"
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Authorization is the header that carries the token.
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}