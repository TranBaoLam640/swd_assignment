package com.freshmart.backend.data_access.entity;

import com.freshmart.backend.enums.authentication_and_user_account.RoleType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Matches {@code Role} in the authentication_and_user_account class diagram
 * (roleId + roleName). {@code roleId} is inherited from
 * {@link BaseEntity#getId()}.
 */
@Entity
@Table(name = "role")
@Getter
@Setter
@NoArgsConstructor
public class Role extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 20)
    private RoleType roleName;
}
