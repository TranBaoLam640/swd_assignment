package com.freshmart.backend.common.exception;

/**
 * Thrown when a request is well-formed but violates a business rule, e.g.
 * duplicate email on register, insufficient stock on checkout, invalid
 * order/refund state transition. Mapped to HTTP 409 (Conflict) by default.
 */
public class BusinessException extends ApiException {

    public BusinessException(String message) {
        super(409, message);
    }

    public BusinessException(int status, String message) {
        super(status, message);
    }
}
