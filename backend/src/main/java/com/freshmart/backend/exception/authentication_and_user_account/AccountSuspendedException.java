package com.freshmart.backend.exception.authentication_and_user_account;

import com.freshmart.backend.common.exception.ApiException;

/** Thrown by AuthServiceImpl.login() when the account status is SUSPENDED/INACTIVE (see login sequence diagram). */
public class AccountSuspendedException extends ApiException {
    public AccountSuspendedException() {
        super(403, "Your account has been suspended");
    }
}
