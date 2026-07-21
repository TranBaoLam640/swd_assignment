package com.freshmart.backend.exception.authentication_and_user_account;

import com.freshmart.backend.common.exception.BusinessException;

/**
 * Thrown by AuthServiceImpl.register() when the requested self-service role
 * isn't allowed to be self-registered (currently: ADMIN — see
 * RegisterRequest's Javadoc for why).
 */
public class InvalidRegistrationRoleException extends BusinessException {
    public InvalidRegistrationRoleException() {
        super(400, "Không thể tự đăng ký với vai trò ADMIN");
    }
}
