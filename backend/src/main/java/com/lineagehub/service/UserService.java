package com.lineagehub.service;

import com.lineagehub.dto.response.UserResponse;
import com.lineagehub.entity.User;
import com.lineagehub.entity.enums.UserStatus;
import com.lineagehub.exception.ResourceNotFoundException;
import com.lineagehub.mapper.UserMapper;
import com.lineagehub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(UserStatus status, String search, Pageable pageable) {
        log.info("Fetching users with status: {}, search: {}", status, search);
        
        Page<User> users = userRepository.findByStatusAndSearch(status, search, pageable);
        return users.map(userMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse approveUser(UUID id) {
        log.info("Approving user with ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        
        user.setStatus(UserStatus.ACTIVE);
        User savedUser = userRepository.save(user);
        
        log.info("User approved successfully: {}", id);
        return userMapper.toResponse(savedUser);
    }

    @Transactional
    public UserResponse deactivateUser(UUID id) {
        log.info("Deactivating user with ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        
        user.setStatus(UserStatus.INACTIVE);
        User savedUser = userRepository.save(user);
        
        log.info("User deactivated successfully: {}", id);
        return userMapper.toResponse(savedUser);
    }

    @Transactional
    public UserResponse activateUser(UUID id) {
        log.info("Activating user with ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        
        user.setStatus(UserStatus.ACTIVE);
        User savedUser = userRepository.save(user);
        
        log.info("User activated successfully: {}", id);
        return userMapper.toResponse(savedUser);
    }

    @Transactional
    public void deleteUser(UUID id) {
        log.info("Deleting user with ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        
        userRepository.delete(user);
        log.info("User deleted successfully: {}", id);
    }
}
