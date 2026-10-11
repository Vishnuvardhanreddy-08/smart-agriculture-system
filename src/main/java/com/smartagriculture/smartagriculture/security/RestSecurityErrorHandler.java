package com.smartagriculture.smartagriculture.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.MessageSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * RestSecurityErrorHandler writes our usual JSON error for security problems:
 *   401 - nobody is logged in (no token, bad token, expired token)
 *   403 - logged in, but not allowed
 * This happens in the filter chain, BEFORE our GlobalExceptionHandler can see it,
 * so this class asks the MessageSource for the text itself.
 */
// @Component tells Spring to create one shared object of this class.
@Component
public class RestSecurityErrorHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final MessageSource messageSource;

    public RestSecurityErrorHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    // Called when someone who is NOT logged in asks for a locked URL.
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        write(request, response, 401, "Unauthorized", "error.unauthorized");
    }

    // Called when a logged-in user is not allowed to do something.
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        write(request, response, 403, "Forbidden", "error.forbidden");
    }

    // Writes the JSON by hand. The message is looked up in the language of the request
    // (the Accept-Language header, which request.getLocale() reads for us).
    private void write(HttpServletRequest request, HttpServletResponse response,
                       int status, String error, String messageKey) throws IOException {
        String message = messageSource.getMessage(messageKey, null, messageKey, request.getLocale());

        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"timestamp\":\"" + LocalDateTime.now()
                + "\",\"status\":" + status
                + ",\"error\":\"" + error
                + "\",\"message\":\"" + escape(message) + "\"}");
    }

    // Makes the text safe inside a JSON string (backslash and double quote).
    private String escape(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}