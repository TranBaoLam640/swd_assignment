package com.freshmart.backend.exception.authentication_and_user_account;

import com.freshmart.backend.common.exception.BusinessException;

/**
 * Thrown by AuthServiceImpl.register() when password and confirmPassword do
 * not match (see UC02 - Sign Up, Alternative Sequence for step 5).
 */
public class PasswordMismatchException extends BusinessException {
    public PasswordMismatchException() {
        super(400, "Mật khẩu xác nhận không khớp với mật khẩu đã nhập.");
    }
}
