package com.freshmart.backend.exception.authentication_and_user_account;

import com.freshmart.backend.common.exception.ApiException;

/** Thrown by AuthServiceImpl.login() when the account status is SUSPENDED/INACTIVE (see login sequence diagram). */
public class AccountSuspendedException extends ApiException {
    public AccountSuspendedException() {
        super(403, "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ bộ phận hỗ trợ để được trợ giúp.");
    }
}
