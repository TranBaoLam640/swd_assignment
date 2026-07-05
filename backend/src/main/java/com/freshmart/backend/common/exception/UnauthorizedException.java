package com.freshmart.backend.common.exception;

/**
 * Thrown when the caller is not authenticated (missing/invalid JWT) or is
 * authenticated but not allowed to perform the action (RBAC failure).
 * Mapped to HTTP 401 by default; pass 403 explicitly for "forbidden" cases.
 */
public class UnauthorizedException extends ApiException {

    public UnauthorizedException(String message) {
        super(401, message);
    }

    public UnauthorizedException(int status, String message) {
        super(status, message);
    }
}
