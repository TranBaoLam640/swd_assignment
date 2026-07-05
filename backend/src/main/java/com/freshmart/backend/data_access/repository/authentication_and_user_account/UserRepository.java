package com.freshmart.backend.data_access.repository.authentication_and_user_account;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.freshmart.backend.data_access.entity.User;

/** Matches UserRepository in the class diagram (findById/save come from JpaRepository). */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
