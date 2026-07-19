package com.freshmart.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.freshmart.backend.data_access.entity.Role;
import com.freshmart.backend.data_access.repository.authentication_and_user_account.RoleRepository;
import com.freshmart.backend.enums.authentication_and_user_account.RoleType;

/**
 * Ensures the 4 fixed roles exist in the DB on startup, since register()
 * looks them up by name. Not in the class diagram — needed because Role
 * rows aren't created anywhere else (no data.sql / migration yet).
 */
@Component
public class RoleSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public RoleSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        for (RoleType roleType : RoleType.values()) {
            roleRepository.findByRoleName(roleType).orElseGet(() -> {
                Role role = new Role();
                role.setRoleName(roleType);
                return roleRepository.save(role);
            });
        }
    }
}
