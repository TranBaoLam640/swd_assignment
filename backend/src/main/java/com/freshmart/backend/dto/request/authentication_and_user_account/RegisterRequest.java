package com.freshmart.backend.dto.request.authentication_and_user_account;

import com.freshmart.backend.enums.authentication_and_user_account.RoleType;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 8, message = "Mật khẩu phải có ít nhất 8 ký tự")
    private String password;

    /**
     * Must match {@link #password} exactly — checked in
     * AuthServiceImpl.register() (see PasswordMismatchException), not here,
     * since Bean Validation has no clean built-in way to compare two fields
     * of the same DTO.
     */
    @NotBlank(message = "Vui lòng xác nhận mật khẩu")
    private String confirmPassword;

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phoneNumber;

    /**
     * Self-service role selection — added so testers/demo users can create
     * MANAGER/SHIPPER accounts through the public register form instead of
     * needing a DB admin to hand-provision them. {@code ADMIN} is
     * deliberately NOT allowed here even if a client sends it: see
     * AuthServiceImpl.register(), which rejects it explicitly. Admin
     * accounts must be provisioned some other way (DB seed, or a future
     * admin-only endpoint) — self-registering as ADMIN would be a real
     * privilege-escalation hole, not just a school-project shortcut.
     */
    @NotNull(message = "Vai trò không được để trống")
    private RoleType role;
}
