package com.freshmart.backend.data_access.repository.authentication_and_user_account;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.freshmart.backend.data_access.entity.Role;
import com.freshmart.backend.enums.authentication_and_user_account.RoleType;

/**
 * Not present in the class diagram, added because AuthServiceImpl needs to
 * look up the Role row to attach to a new User at registration time.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(RoleType roleName);
}
