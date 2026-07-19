package com.freshmart.backend.exception.authentication_and_user_account;

import com.freshmart.backend.common.exception.ApiException;

/** Thrown by AuthServiceImpl.login() when the password does not match (see login sequence diagram). */
public class InvalidCredentialsException extends ApiException {
    public InvalidCredentialsException() {
        super(401, "Invalid email or password");
    }
}
