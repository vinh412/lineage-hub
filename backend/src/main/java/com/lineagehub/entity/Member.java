package com.lineagehub.entity;

import com.lineagehub.entity.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "full_name", nullable = false, length = 255, columnDefinition = "VARCHAR(255)")
    private String fullName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;
    
    @Column(name = "birth_date")
    private LocalDate birthDate;
    
    @Column(name = "death_date")
    private LocalDate deathDate;
    
    @Column(name = "is_blood_relative", nullable = false)
    @Builder.Default
    private Boolean isBloodRelative = true;
    
    @Column(name = "branch_name")
    private String branchName;
    
    private String address;
    
    private String phone;
    
    private String email;
    
    @Column(name = "avatar_url")
    private String avatarUrl;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    private Integer generation;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
    
    @Column(name = "updated_at")
    private Instant updatedAt;
    
    // Relationships
    @OneToMany(mappedBy = "fromMember", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Relationship> relationshipsAsParent = new ArrayList<>();
    
    @OneToMany(mappedBy = "toMember", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Relationship> relationshipsAsChild = new ArrayList<>();
    
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
    public boolean isDeceased() {
        return deathDate != null;
    }
    
    public Integer getBirthYear() {
        return birthDate != null ? birthDate.getYear() : null;
    }
    
    public Integer getDeathYear() {
        return deathDate != null ? deathDate.getYear() : null;
    }
}
