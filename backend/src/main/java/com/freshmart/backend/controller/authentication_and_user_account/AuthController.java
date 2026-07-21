package com.freshmart.backend.controller.authentication_and_user_account;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freshmart.backend.common.dto.ApiResponse;
import com.freshmart.backend.dto.request.authentication_and_user_account.LoginRequest;
import com.freshmart.backend.dto.request.authentication_and_user_account.RegisterRequest;
import com.freshmart.backend.dto.response.authentication_and_user_account.UserResponse;
import com.freshmart.backend.service.interfaces.authentication_and_user_account.AuthService;

import jakarta.validation.Valid;

/** Scoped to login + register for now; logout/forgotPassword/etc. added in a later phase. */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<UserResponse> login(@Valid @RequestBody LoginRequest request) {
        UserResponse response = authService.login(request.getEmail(), request.getPassword());
        return ApiResponse.success(200, "Login successful", response);
    }

    @PostMapping("/register")
    public ApiResponse<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ApiResponse.success(201, "Đăng ký thành công, vui lòng đăng nhập", null);
    }

    /** Requires a valid Bearer token (see SecurityConfig — only login/register are public). */
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@AuthenticationPrincipal Long userId) {
        authService.logout(userId);
        return ApiResponse.success(200, "Logout successful", null);
    }
}
