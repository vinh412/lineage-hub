package com.lineagehub.service;

import com.lineagehub.config.JwtConfig;
import com.lineagehub.dto.request.LoginRequest;
import com.lineagehub.dto.request.RegisterRequest;
import com.lineagehub.dto.response.AuthResponse;
import com.lineagehub.dto.response.UserResponse;
import com.lineagehub.entity.User;
import com.lineagehub.entity.UserRole;
import com.lineagehub.entity.enums.Role;
import com.lineagehub.entity.enums.UserStatus;
import com.lineagehub.exception.BusinessException;
import com.lineagehub.exception.DuplicateResourceException;
import com.lineagehub.mapper.UserMapper;
import com.lineagehub.repository.UserRepository;
import com.lineagehub.security.CustomUserDetails;
import com.lineagehub.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserMapper userMapper;
    private final JwtConfig jwtConfig;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());
        
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email đã được sử dụng");
        }

        // Create new user
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .status(UserStatus.PENDING)
                .roles(new ArrayList<>())
                .build();

        // Create default USER role
        UserRole userRole = UserRole.builder()
                .user(user)
                .role(Role.USER)
                .managedMember(null)
                .build();
        
        user.getRoles().add(userRole);

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        return userMapper.toResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("Attempting login for user: {}", request.getEmail());

        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Get user details
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        
        // Check if user is active
        if (!userDetails.isActive()) {
            throw new BusinessException("Tài khoản đang chờ phê duyệt hoặc đã bị vô hiệu hóa");
        }

        // Generate JWT token
        String jwt = tokenProvider.generateToken(authentication);
        
        // Load full user details with roles
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new BusinessException("User not found"));
        
        UserResponse userResponse = userMapper.toResponse(user);

        log.info("User logged in successfully: {}", request.getEmail());

        return AuthResponse.builder()
                .accessToken(jwt)
                .tokenType("Bearer")
                .expiresIn(jwtConfig.getExpiration())
                .user(userResponse)
                .build();
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));
        
        return userMapper.toResponse(user);
    }
}
