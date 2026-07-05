package com.freshmart.backend.common.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.freshmart.backend.common.dto.ApiResponse;

/**
 * Central place converting every exception into the standard
 * {@link ApiResponse} error envelope, so controllers never need their own
 * try/catch or {@code @ExceptionHandler}.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** Business/API errors thrown deliberately by services (404, 409, 401, ...). */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(ApiResponse.error(ex.getStatus(), ex.getMessage()));
    }

    /** Bean Validation failures on @Valid request bodies -> 400 with field-level errors. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        List<com.freshmart.backend.common.dto.FieldError> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toFieldError)
                .toList();

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(400, "Validation failed", errors));
    }

    /** Spring Security RBAC failure -> 403. */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(403, "You do not have permission to perform this action"));
    }

    /** Fallback for anything unexpected -> 500, message kept generic for the client. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, "Unexpected error occurred"));
    }

    private com.freshmart.backend.common.dto.FieldError toFieldError(FieldError springFieldError) {
        return new com.freshmart.backend.common.dto.FieldError(
                springFieldError.getField(),
                springFieldError.getDefaultMessage());
    }
}
