package com.freshmart.backend.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Single field-level validation error, exposed inside {@link ApiResponse#getErrors()}.
 * Kept intentionally small (field + message only) so we never leak Spring's
 * internal {@code org.springframework.validation.FieldError} (rejected value,
 * error codes, object name, ...) straight into the API contract.
 *
 * <p>Populated by the global exception handler when converting a
 * {@code MethodArgumentNotValidException} / {@code BindingResult} into the
 * API error envelope, e.g.:
 * <pre>{@code
 * List<FieldError> errors = bindingResult.getFieldErrors().stream()
 *     .map(e -> new FieldError(e.getField(), e.getDefaultMessage()))
 *     .toList();
 * }</pre>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FieldError {

    /** Name of the request field that failed validation, e.g. "email". */
    private String field;

    /** Human-readable validation message, e.g. "Email đã được sử dụng". */
    private String message;
}
