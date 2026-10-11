package com.smartagriculture.smartagriculture.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * ApiError is the ONE shape used for every error response.
 * The React app can always read "message" and show it to the farmer.
 */
public class ApiError {

    // When the error happened.
    private final LocalDateTime timestamp = LocalDateTime.now();

    // HTTP status number, for example 404.
    private final int status;

    // Short status name, for example "Not Found".
    private final String error;

    // Clear message for people.
    private final String message;

    // Only used for validation errors: field name -> what is wrong with it.
    // @JsonInclude hides this field from the JSON when it is empty.
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final Map<String, String> fieldErrors;

    public ApiError(int status, String error, String message, Map<String, String> fieldErrors) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.fieldErrors = fieldErrors;
    }

    // Only getters are needed to create the JSON.
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
}