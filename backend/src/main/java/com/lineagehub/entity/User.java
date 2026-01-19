package com.lineagehub.entity;

import com.lineagehub.entity.enums.Role;
import com.lineagehub.entity.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, unique = true, length = 255, columnDefinition = "VARCHAR(255)")
    private String email;
    
    @Column(name = "password_hash", nullable = false, length = 255, columnDefinition = "VARCHAR(255)")
    private String passwordHash;
    
    @Column(name = "full_name", nullable = false, length = 255, columnDefinition = "VARCHAR(255)")
    private String fullName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UserStatus status = UserStatus.PENDING;
    
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
    
    @Column(name = "updated_at")
    private Instant updatedAt;
    
    // Relationships
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<UserRole> roles = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
    
    // Helper methods
    public boolean isSuperAdmin() {
        return roles.stream().anyMatch(r -> r.getRole() == Role.SUPER_ADMIN);
    }
    
    public boolean isBranchAdmin() {
        return roles.stream().anyMatch(r -> r.getRole() == Role.BRANCH_ADMIN);
    }
    
    public boolean isUser() {
        return roles.stream().anyMatch(r -> r.getRole() == Role.USER);
    }
    
    public List<UUID> getManagedMemberIds() {
        return roles.stream()
            .filter(r -> r.getRole() == Role.BRANCH_ADMIN && r.getManagedMember() != null)
            .map(r -> r.getManagedMember().getId())
            .collect(Collectors.toList());
    }
    
    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }
}
