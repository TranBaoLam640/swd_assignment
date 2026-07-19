package com.freshmart.backend.service.interfaces.authentication_and_user_account;

import com.freshmart.backend.dto.request.authentication_and_user_account.RegisterRequest;
import com.freshmart.backend.dto.response.authentication_and_user_account.UserResponse;

/**
 * Scoped to login + register for this implementation phase.
 * logout/forgotPassword/verifyResetToken/resetPassword/changePassword
 * (also on the diagram) will be added once PasswordResetToken is built.
 */
public interface AuthService {

    UserResponse login(String email, String password);

    boolean register(RegisterRequest request);

    boolean logout(Long userId);
}
