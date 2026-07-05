package com.freshmart.backend.exception.authentication_and_user_account;

import com.freshmart.backend.common.exception.BusinessException;

/** Thrown by AuthServiceImpl.register() when the email is already registered (see register sequence diagram). */
public class DuplicateEmailException extends BusinessException {
    public DuplicateEmailException(String email) {
        super("Email already registered: " + email);
    }
}
