package com.freshmart.backend.data_access.entity;

import com.freshmart.backend.enums.authentication_and_user_account.UserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Matches {@code User} in the class diagram (userId, email, passwordHash,
 * fullName, phoneNumber, status, createdAt). {@code userId} is inherited
 * from {@link BaseEntity#getId()}; table named "users" (not "user") to
 * avoid a MySQL reserved-word conflict.
 *
 * <p>The diagram shows register()/updateProfile()/viewProfile() under
 * User, but per the accompanying sequence diagrams that behavior actually
 * lives in AuthServiceImpl/UserServiceImpl — kept as a plain entity here,
 * consistent with the Controller-Service-Repository layering used
 * everywhere else in the SDS.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity {

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false, length = 150)
    private String fullName;

    @Column(length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
}
