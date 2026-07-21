package com.freshmart.backend.exception.authentication_and_user_account;

import com.freshmart.backend.common.exception.BusinessException;

/**
 * Thrown by AuthServiceImpl.register() when the email is already registered
 * (see UC02 - Sign Up, Alternative Sequence for step 6).
 *
 * <p>The email argument is accepted for the caller's convenience (e.g. if it
 * needs to be logged server-side later) but is deliberately NOT included in
 * the client-facing message, to avoid echoing user-supplied data back in an
 * error response.
 */
public class DuplicateEmailException extends BusinessException {
    public DuplicateEmailException(String email) {
        super("Email đã được sử dụng để đăng ký tài khoản khác.");
    }
}
