package com.lineagehub.repository;

import com.lineagehub.entity.Relationship;
import com.lineagehub.entity.enums.RelationshipType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RelationshipRepository extends JpaRepository<Relationship, UUID> {
    
    List<Relationship> findByFromMemberId(UUID fromMemberId);
    
    List<Relationship> findByToMemberId(UUID toMemberId);
    
    List<Relationship> findByFromMemberIdAndRelationshipType(UUID fromMemberId, RelationshipType type);
    
    List<Relationship> findByToMemberIdAndRelationshipType(UUID toMemberId, RelationshipType type);
    
    boolean existsByFromMemberIdAndToMemberIdAndRelationshipType(UUID fromMemberId, UUID toMemberId, RelationshipType type);
    
    boolean existsByFromMemberIdOrToMemberId(UUID fromMemberId, UUID toMemberId);
    
    // Get all parent-child relationships for a member
    @Query("SELECT r FROM Relationship r WHERE r.toMember.id = :memberId AND r.relationshipType = 'PARENT_CHILD'")
    List<Relationship> findParentsByMemberId(@Param("memberId") UUID memberId);
    
    @Query("SELECT r FROM Relationship r WHERE r.fromMember.id = :memberId AND r.relationshipType = 'PARENT_CHILD'")
    List<Relationship> findChildrenByMemberId(@Param("memberId") UUID memberId);
    
    // Get all spouse relationships for a member
    @Query("SELECT r FROM Relationship r WHERE (r.fromMember.id = :memberId OR r.toMember.id = :memberId) AND r.relationshipType = 'SPOUSE'")
    List<Relationship> findSpousesByMemberId(@Param("memberId") UUID memberId);
    
    // Get descendants (recursive) - using native query with CTE
    @Query(value = """
        WITH RECURSIVE descendants AS (
            SELECT m.id, m.full_name, 0 AS depth
            FROM members m
            WHERE m.id = :rootMemberId
            
            UNION
            
            SELECT m.id, m.full_name, d.depth + 1
            FROM members m
            INNER JOIN relationships r ON r.to_member_id = m.id
            INNER JOIN descendants d ON r.from_member_id = d.id
            WHERE r.relationship_type = 'PARENT_CHILD'
            AND d.depth < :maxDepth
        )
        SELECT id FROM descendants
        """, nativeQuery = true)
    List<UUID> findDescendantIds(@Param("rootMemberId") UUID rootMemberId, @Param("maxDepth") int maxDepth);
    
    // Get subtree including spouses - more complex query
    @Query(value = """
        WITH RECURSIVE subtree AS (
            SELECT m.id, m.is_blood_relative, 0 AS depth
            FROM members m
            WHERE m.id = :rootMemberId
            
            UNION
            
            SELECT m.id, m.is_blood_relative, s.depth + 1
            FROM members m
            INNER JOIN relationships r ON r.to_member_id = m.id
            INNER JOIN subtree s ON r.from_member_id = s.id
            WHERE (
                (r.relationship_type = 'PARENT_CHILD' AND s.is_blood_relative = true)
                OR r.relationship_type = 'SPOUSE'
            )
            AND s.depth < :maxDepth
            AND m.id NOT IN (SELECT id FROM subtree)
        )
        SELECT id FROM subtree
        """, nativeQuery = true)
    List<UUID> findSubtreeIdsIncludingSpouses(@Param("rootMemberId") UUID rootMemberId, @Param("maxDepth") int maxDepth);
    
    // Delete all relationships for a member
    void deleteByFromMemberIdOrToMemberId(UUID fromMemberId, UUID toMemberId);
}
