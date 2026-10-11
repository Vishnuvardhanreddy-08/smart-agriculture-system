package com.smartagriculture.smartagriculture.exception;

import com.smartagriculture.smartagriculture.dto.ApiError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * GlobalExceptionHandler catches errors from ALL controllers
 * and turns them into one clear JSON shape (ApiError).
 * The "message" is written in the language the farmer asked for (Accept-Language header).
 */
// @RestControllerAdvice = "watch every controller, and return JSON for the errors".
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Logger: writes the REAL error to the IntelliJ console (never sent to the browser).
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // MessageSource finds a message by its key, in the right language file.
    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    // Our own errors: 404, 409, 400, 401, 403... The key is turned into a sentence here.
    // (ResourceNotFoundException and ConflictException are kinds of ApiException.)
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> handleApiException(ApiException ex) {
        return build(ex.getStatus(), text(ex.getMessageKey(), ex.getArgs()), null);
    }

    // 400: @Valid failed (blank name, amount <= 0...). We list every wrong field.
    // The field messages are already in the right language (Step 24b).
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> fieldErrors.putIfAbsent(error.getField(), error.getDefaultMessage()));
        return build(HttpStatus.BAD_REQUEST, text("error.validation"), fieldErrors);
    }

    // 400: the JSON body is broken, missing, or has a wrong value type (like an impossible date).
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadableBody(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST, text("error.badBody"), null);
    }

    // 400: a URL value has the wrong type (for example /farmers/abc or from=06-10-2026).
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return build(HttpStatus.BAD_REQUEST, text("error.invalidValue", ex.getValue(), ex.getName()), null);
    }

    // 400: a required URL parameter is missing (for example ?farmerId=1 was forgotten).
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingParameter(MissingServletRequestParameterException ex) {
        return build(HttpStatus.BAD_REQUEST, text("error.missingParam", ex.getParameterName()), null);
    }

    // Errors thrown by Spring itself with ResponseStatusException (a safety net).
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatus(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        String message = ex.getReason() != null ? ex.getReason() : status.getReasonPhrase();
        return build(status, message, null);
    }

    // 409: the database refused the change because records are linked
    // (safety net, for example deleting a farmer who still has crops).
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());
        return build(HttpStatus.CONFLICT, text("error.dataIntegrity"), null);
    }

    // 405: wrong HTTP method (for example DELETE on a URL that only allows GET).
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, text("error.methodNotAllowed", ex.getMethod()), null);
    }

    // 415: the request body was not sent as JSON.
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiError> handleMediaType(HttpMediaTypeNotSupportedException ex) {
        return build(HttpStatus.UNSUPPORTED_MEDIA_TYPE, text("error.mediaType"), null);
    }

    // 404: the URL does not exist at all.
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> handleNoResource(NoResourceFoundException ex) {
        return build(HttpStatus.NOT_FOUND, text("error.endpointNotFound"), null);
    }

    // 500: anything unexpected. The farmer sees a safe, generic message.
    // The REAL error (with details) is only written to the console log (in English, for developers).
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAnythingElse(Exception ex) {
        log.error("Unexpected error", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, text("error.server"), null);
    }

    // Helper: finds the message for a KEY in the current request's language.
    // The values (args) are turned into TEXT first, so an id like 1000 does not become "1,000".
    // If the key is missing from the files, the key itself is shown (so a gap is easy to notice).
    private String text(String key, Object... args) {
        Object[] values = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            values[i] = String.valueOf(args[i]);
        }
        return messageSource.getMessage(key, values, key, LocaleContextHolder.getLocale());
    }

    // Helper: builds the ApiError and wraps it in a ResponseEntity with the right status.
    private ResponseEntity<ApiError> build(HttpStatus status, String message, Map<String, String> fieldErrors) {
        ApiError body = new ApiError(status.value(), status.getReasonPhrase(), message, fieldErrors);
        return ResponseEntity.status(status).body(body);
    }
}