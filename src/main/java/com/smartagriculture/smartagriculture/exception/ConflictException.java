package com.smartagriculture.smartagriculture.exception;

import org.springframework.http.HttpStatus;

/**
 * We throw this when a request is understood but cannot be done
 * because it conflicts with existing data.
 * It is an ApiException with status 409. Example:
 *   throw new ConflictException("crop.hasRecords", cropName);
 */
public class ConflictException extends ApiException {

    public ConflictException(String messageKey, Object... args) {
        super(HttpStatus.CONFLICT, messageKey, args);
    }
}