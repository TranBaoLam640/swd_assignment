package com.freshmart.backend.dto.response.authentication_and_user_account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserInfoResponse {
    private Long userId;
    private String email;
    private String fullName;
    private String role;
}
