package com.smartagriculture.smartagriculture.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JwtAuthenticationFilter runs on EVERY request, before the controller.
 * It looks for "Authorization: Bearer <token>". If the token is valid, it tells
 * Spring Security who is logged in. If not, it does nothing, and Spring Security
 * will answer 401 for any locked URL.
 *
 * (OncePerRequestFilter guarantees it runs exactly once per request.)
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // The browser or Postman sends:  Authorization: Bearer eyJhbGciOi...
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7).trim();
            Long farmerId = jwtService.extractFarmerId(token);

            if (farmerId != null) {
                // Valid token: record "farmer N is logged in" for the rest of this request.
                // The "principal" (who) is the farmer id. No password, and no roles are needed.
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(farmerId, null, List.of());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Always continue to the next filter. Locked URLs are refused later if nobody is logged in.
        filterChain.doFilter(request, response);
    }
}