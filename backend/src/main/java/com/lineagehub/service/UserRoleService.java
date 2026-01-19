package com.lineagehub.service;

import com.lineagehub.dto.request.AddUserRoleRequest;
import com.lineagehub.dto.request.UpdateUserRolesRequest;
import com.lineagehub.dto.response.UserRoleResponse;
import com.lineagehub.entity.Member;
import com.lineagehub.entity.User;
import com.lineagehub.entity.UserRole;
import com.lineagehub.entity.enums.Role;
import com.lineagehub.exception.BusinessException;
import com.lineagehub.exception.ResourceNotFoundException;
import com.lineagehub.mapper.UserRoleMapper;
import com.lineagehub.repository.MemberRepository;
import com.lineagehub.repository.UserRepository;
import com.lineagehub.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final UserRoleMapper userRoleMapper;

    @Transactional(readOnly = true)
    public List<UserRoleResponse> getUserRoles(UUID userId) {
        log.info("Fetching roles for user: {}", userId);
        
        List<UserRole> roles = userRoleRepository.findByUserId(userId);
        return roles.stream()
                .map(userRoleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserRoleResponse addUserRole(UUID userId, AddUserRoleRequest request, UUID currentUserId) {
        log.info("Adding role {} to user: {}", request.getRole(), userId);
        
        // Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", currentUserId));
        
        // Validate role-managed_member relationship
        validateRoleManagedMember(request.getRole(), request.getManagedMemberId());
        
        // Check for duplicate role
        if (request.getRole() == Role.SUPER_ADMIN || request.getRole() == Role.USER) {
            // Only one SUPER_ADMIN or USER role allowed
            if (userRoleRepository.existsByUserIdAndRole(userId, request.getRole())) {
                throw new BusinessException(String.format("User đã có role %s", request.getRole()));
            }
        } else if (request.getRole() == Role.BRANCH_ADMIN) {
            // Can have multiple BRANCH_ADMIN roles, but not for same managed_member
            if (userRoleRepository.existsByUserIdAndRoleAndManagedMemberId(
                    userId, Role.BRANCH_ADMIN, request.getManagedMemberId())) {
                throw new BusinessException("User đã có role BRANCH_ADMIN cho member này");
            }
        }
        
        // Get managed member if applicable
        Member managedMember = null;
        if (request.getManagedMemberId() != null) {
            managedMember = memberRepository.findById(request.getManagedMemberId())
                    .orElseThrow(() -> new ResourceNotFoundException("Member", request.getManagedMemberId()));
        }
        
        // Create new role
        UserRole userRole = UserRole.builder()
                .user(user)
                .role(request.getRole())
                .managedMember(managedMember)
                .createdBy(currentUser)
                .build();
        
        UserRole savedRole = userRoleRepository.save(userRole);
        log.info("Role {} added successfully to user: {}", request.getRole(), userId);
        
        return userRoleMapper.toResponse(savedRole);
    }

    @Transactional
    public List<UserRoleResponse> updateUserRoles(UUID userId, UpdateUserRolesRequest request, UUID currentUserId) {
        log.info("Updating all roles for user: {}", userId);
        
        // Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", currentUserId));
        
        // Delete all existing roles
        userRoleRepository.deleteByUserId(userId);
        userRoleRepository.flush();
        
        // Add new roles
        List<UserRole> newRoles = request.getRoles().stream()
                .map(roleRequest -> {
                    validateRoleManagedMember(roleRequest.getRole(), roleRequest.getManagedMemberId());
                    
                    Member managedMember = null;
                    if (roleRequest.getManagedMemberId() != null) {
                        managedMember = memberRepository.findById(roleRequest.getManagedMemberId())
                                .orElseThrow(() -> new ResourceNotFoundException("Member", roleRequest.getManagedMemberId()));
                    }
                    
                    return UserRole.builder()
                            .user(user)
                            .role(roleRequest.getRole())
                            .managedMember(managedMember)
                            .createdBy(currentUser)
                            .build();
                })
                .collect(Collectors.toList());
        
        List<UserRole> savedRoles = userRoleRepository.saveAll(newRoles);
        log.info("Updated {} roles for user: {}", savedRoles.size(), userId);
        
        return savedRoles.stream()
                .map(userRoleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteUserRole(UUID userId, UUID roleId) {
        log.info("Deleting role {} from user: {}", roleId, userId);
        
        UserRole userRole = userRoleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("UserRole", roleId));
        
        // Verify role belongs to user
        if (!userRole.getUser().getId().equals(userId)) {
            throw new BusinessException("Role không thuộc về user này");
        }
        
        // Check if this is the last role
        List<UserRole> userRoles = userRoleRepository.findByUserId(userId);
        if (userRoles.size() == 1) {
            throw new BusinessException("Không thể xóa role cuối cùng của user");
        }
        
        userRoleRepository.delete(userRole);
        log.info("Role deleted successfully");
    }

    private void validateRoleManagedMember(Role role, UUID managedMemberId) {
        if (role == Role.SUPER_ADMIN || role == Role.USER) {
            if (managedMemberId != null) {
                throw new BusinessException(String.format("%s không được có managed_member_id", role));
            }
        } else if (role == Role.BRANCH_ADMIN) {
            if (managedMemberId == null) {
                throw new BusinessException("BRANCH_ADMIN bắt buộc phải có managed_member_id");
            }
        }
    }
}
