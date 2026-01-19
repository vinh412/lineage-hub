package com.lineagehub.entity;

import com.lineagehub.entity.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRole {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "managed_member_id")
    private Member managedMember;  // Only for BRANCH_ADMIN
    
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
