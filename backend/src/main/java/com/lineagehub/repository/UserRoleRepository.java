package com.lineagehub.repository;

import com.lineagehub.entity.UserRole;
import com.lineagehub.entity.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {
    
    List<UserRole> findByUserId(UUID userId);
    
    List<UserRole> findByUserIdAndRole(UUID userId, Role role);
    
    @Query("SELECT ur.managedMember.id FROM UserRole ur WHERE ur.user.id = :userId AND ur.role = 'BRANCH_ADMIN' AND ur.managedMember IS NOT NULL")
    List<UUID> findManagedMemberIdsByUserId(@Param("userId") UUID userId);
    
    boolean existsByUserIdAndRole(UUID userId, Role role);
    
    boolean existsByUserIdAndRoleAndManagedMemberId(UUID userId, Role role, UUID managedMemberId);
    
    void deleteByUserIdAndRole(UUID userId, Role role);
    
    void deleteByUserId(UUID userId);
}
