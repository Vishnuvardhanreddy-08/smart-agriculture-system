package com.smartagriculture.smartagriculture.exception;

import org.springframework.http.HttpStatus;

/**
 * ApiException is the ONE kind of error our code throws on purpose.
 * It carries:
 *   - the HTTP status (404, 409, 400, 401, 403...)
 *   - a message KEY (for example "crop.notFound"), NOT a sentence
 *   - values to put into the message (for example the crop id)
 * GlobalExceptionHandler turns the key into a sentence in the farmer's language.
 */
public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final String messageKey;
    private final Object[] args;

    // "Object... args" (varargs) means: zero, one or many values can be given.
    public ApiException(HttpStatus status, String messageKey, Object... args) {
        super(messageKey); // the key is also the technical message (useful in the logs)
        this.status = status;
        this.messageKey = messageKey;
        this.args = args;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public Object[] getArgs() {
        return args;
    }
}