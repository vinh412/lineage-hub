package com.lineagehub.entity;

import com.lineagehub.entity.enums.RelationshipType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "relationships")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Relationship {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_member_id", nullable = false)
    private Member fromMember;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_member_id", nullable = false)
    private Member toMember;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "relationship_type", nullable = false)
    private RelationshipType relationshipType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
