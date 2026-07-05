package com.freshmart.backend.common.dto;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Standard response envelope wrapping every non-paginated REST API response
 * (controllers should return {@code ApiResponse<T>} instead of a bare DTO).
 *
 * <p>Success example:
 * <pre>{@code
 * {
 *   "success": true,
 *   "code": 200,
 *   "message": "Login successful",
 *   "data": { ... },
 *   "timestamp": "2026-07-05T10:30:00Z"
 * }
 * }</pre>
 *
 * <p>Error example ({@code errors} only populated for validation failures):
 * <pre>{@code
 * {
 *   "success": false,
 *   "code": 400,
 *   "message": "Email already exists",
 *   "errors": [ { "field": "email", "message": "Email đã được sử dụng" } ],
 *   "timestamp": "2026-07-05T10:30:00Z"
 * }
 * }</pre>
 *
 * <p>Not used for the VNPAY IPN webhook endpoint ({@code PaymentWebhookController}),
 * which must reply in the format VNPAY expects.
 *
 * @param <T> type of the payload carried in {@link #getData()}
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    /** Whether the request succeeded. */
    private boolean success;

    /** HTTP status code mirrored in the body, e.g. 200, 400, 401, 403, 404, 409, 500. */
    private int code;

    /** Human-readable summary of the result. */
    private String message;

    /** Response payload; {@code null} on error responses. */
    private T data;

    /** Field-level validation errors; {@code null}/omitted unless {@code success} is false due to invalid input. */
    private List<FieldError> errors;

    /** Server timestamp the response was produced at. */
    private Instant timestamp;

    public static <T> ApiResponse<T> success(int code, String message, T data) {
        return new ApiResponse<>(true, code, message, data, null, Instant.now());
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(200, "OK", data);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(false, code, message, null, null, Instant.now());
    }

    public static <T> ApiResponse<T> error(int code, String message, List<FieldError> errors) {
        return new ApiResponse<>(false, code, message, null, errors, Instant.now());
    }
}
