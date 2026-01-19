package com.lineagehub.repository;

import com.lineagehub.entity.AuditLog;
import com.lineagehub.entity.enums.AuditAction;
import com.lineagehub.entity.enums.EntityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    
    Page<AuditLog> findByEntityTypeAndEntityId(EntityType entityType, UUID entityId, Pageable pageable);
    
    Page<AuditLog> findByUserId(UUID userId, Pageable pageable);
    
    Page<AuditLog> findByEntityType(EntityType entityType, Pageable pageable);
    
    @Query("SELECT a FROM AuditLog a WHERE " +
           "(:entityType IS NULL OR a.entityType = :entityType) AND " +
           "(:entityId IS NULL OR a.entityId = :entityId) AND " +
           "(:userId IS NULL OR a.user.id = :userId) AND " +
           "(:action IS NULL OR a.action = :action) AND " +
           "(:from IS NULL OR a.createdAt >= :from) AND " +
           "(:to IS NULL OR a.createdAt <= :to)")
    Page<AuditLog> findByFilters(@Param("entityType") EntityType entityType,
                                   @Param("entityId") UUID entityId,
                                   @Param("userId") UUID userId,
                                   @Param("action") AuditAction action,
                                   @Param("from") Instant from,
                                   @Param("to") Instant to,
                                   Pageable pageable);
    
    List<AuditLog> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(EntityType entityType, UUID entityId);
}
