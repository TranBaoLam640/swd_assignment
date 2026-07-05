package com.freshmart.backend.common.exception;

import lombok.Getter;

/**
 * Base unchecked exception for all business/API errors. Thrown from any
 * service/controller and caught by {@link GlobalExceptionHandler}, which
 * converts it into an {@code ApiResponse.error(...)} envelope.
 *
 * <p>Module-specific exceptions (under {@code exception.<module>}) should
 * extend this class instead of {@link RuntimeException} directly, so they
 * are all handled the same way without extra {@code @ExceptionHandler}
 * methods per module.
 */
@Getter
public class ApiException extends RuntimeException {

    /** HTTP status code to return, e.g. 400, 401, 403, 404, 409. */
    private final int status;

    public ApiException(int status, String message) {
        super(message);
        this.status = status;
    }
}
