package com.smartagriculture.smartagriculture.exception;

import org.springframework.http.HttpStatus;

/**
 * We throw this when a record is not found (for example farmer id 99).
 * It is an ApiException with status 404. Example:
 *   throw new ResourceNotFoundException("crop.notFound", cropId, farmerId);
 */
public class ResourceNotFoundException extends ApiException {

    public ResourceNotFoundException(String messageKey, Object... args) {
        super(HttpStatus.NOT_FOUND, messageKey, args);
    }
}