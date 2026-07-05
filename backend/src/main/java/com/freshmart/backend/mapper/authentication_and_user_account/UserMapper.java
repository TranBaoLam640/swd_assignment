package com.freshmart.backend.mapper.authentication_and_user_account;

import org.springframework.stereotype.Component;

import com.freshmart.backend.data_access.entity.User;
import com.freshmart.backend.dto.response.authentication_and_user_account.UserInfoResponse;

@Component
public class UserMapper {

    public UserInfoResponse toUserInfoResponse(User user) {
        return new UserInfoResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().getRoleName().name());
    }
}
