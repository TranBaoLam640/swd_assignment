package com.freshmart.backend.service.impl.authentication_and_user_account;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.freshmart.backend.common.exception.ResourceNotFoundException;
import com.freshmart.backend.data_access.entity.Role;
import com.freshmart.backend.data_access.entity.User;
import com.freshmart.backend.data_access.repository.authentication_and_user_account.RoleRepository;
import com.freshmart.backend.data_access.repository.authentication_and_user_account.UserRepository;
import com.freshmart.backend.dto.request.authentication_and_user_account.RegisterRequest;
import com.freshmart.backend.dto.response.authentication_and_user_account.UserResponse;
import com.freshmart.backend.enums.authentication_and_user_account.RoleType;
import com.freshmart.backend.enums.authentication_and_user_account.UserStatus;
import com.freshmart.backend.exception.authentication_and_user_account.AccountSuspendedException;
import com.freshmart.backend.exception.authentication_and_user_account.DuplicateEmailException;
import com.freshmart.backend.exception.authentication_and_user_account.InvalidCredentialsException;
import com.freshmart.backend.mapper.authentication_and_user_account.UserMapper;
import com.freshmart.backend.security.JwtProvider;
import com.freshmart.backend.service.interfaces.authentication_and_user_account.AuthService;

/**
 * Follows the login/register sequence diagrams step by step.
 * EmailService is intentionally left out for now (per request) — add back
 * sendWelcomeEmail() in register() once that dependency is wanted again.
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final UserMapper userMapper;

    public AuthServiceImpl(UserRepository userRepository,
                            RoleRepository roleRepository,
                            PasswordEncoder passwordEncoder,
                            JwtProvider jwtProvider,
                            UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponse login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new AccountSuspendedException();
        }

        String token = jwtProvider.generateToken(user.getId(), user.getRole().getRoleName().name());
        return new UserResponse(token, userMapper.toUserInfoResponse(user));
    }

    @Override
    @Transactional
    public boolean register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        Role customerRole = roleRepository.findByRoleName(RoleType.CUSTOMER)
                .orElseThrow(() -> new IllegalStateException("DeER fault role CUSTOMis not seeded"));

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setStatus(UserStatus.ACTIVE);
        user.setRole(customerRole);

        userRepository.save(user);

        return true;
    }

    @Override
    public boolean logout(Long userId) {
        // Stateless JWT, no Redis blacklist by design: nothing to invalidate
        // server-side. The client is responsible for discarding the token.
        return true;
    }
}
