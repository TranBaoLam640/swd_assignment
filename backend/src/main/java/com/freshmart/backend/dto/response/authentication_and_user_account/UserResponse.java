package com.freshmart.backend.dto.response.authentication_and_user_account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/** Matches the login sequence diagram: UserResponse(accessToken, userInfo). */
@Getter
@Setter
@AllArgsConstructor
public class UserResponse {
    private String accessToken;
    private UserInfoResponse userInfo;
}
