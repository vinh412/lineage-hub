package com.lineagehub.service;

import com.lineagehub.entity.Relationship;
import com.lineagehub.entity.User;
import com.lineagehub.entity.enums.RelationshipType;
import com.lineagehub.repository.RelationshipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorizationService {

    private final RelationshipRepository relationshipRepository;

    /**
     * Check if user can edit a specific member
     * SUPER_ADMIN: can edit all members
     * BRANCH_ADMIN: can edit members in their managed subtrees
     * USER: cannot edit any members
     */
    @Transactional(readOnly = true)
    public boolean canEditMember(User user, UUID memberId) {
        // SUPER_ADMIN can edit all
        if (user.isSuperAdmin()) {
            return true;
        }

        // USER role cannot edit
        if (user.isUser() && !user.isBranchAdmin()) {
            return false;
        }

        // BRANCH_ADMIN: check if member is in any managed subtree
        List<UUID> managedMemberIds = user.getManagedMemberIds();
        for (UUID managedMemberId : managedMemberIds) {
            Set<UUID> subtreeIds = getSubtreeIds(managedMemberId);
            if (subtreeIds.contains(memberId)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if user can edit/delete a relationship
     * SUPER_ADMIN: can edit all relationships
     * BRANCH_ADMIN: can edit relationships within their subtrees,
     *               BUT cannot edit/delete parent->managed_member relationships
     */
    @Transactional(readOnly = true)
    public boolean canEditRelationship(User user, Relationship relationship) {
        // SUPER_ADMIN can edit all
        if (user.isSuperAdmin()) {
            return true;
        }

        // USER role cannot edit
        if (user.isUser() && !user.isBranchAdmin()) {
            return false;
        }

        // BRANCH_ADMIN: check permissions
        List<UUID> managedMemberIds = user.getManagedMemberIds();
        for (UUID managedMemberId : managedMemberIds) {
            // Cannot edit/delete parent->managed_member relationship
            if (relationship.getRelationshipType() == RelationshipType.PARENT_CHILD
                    && relationship.getToMember().getId().equals(managedMemberId)) {
                continue; // Try next managed member
            }

            // Check if both members are in subtree
            Set<UUID> subtreeIds = getSubtreeIds(managedMemberId);
            if (subtreeIds.contains(relationship.getFromMember().getId())
                    && subtreeIds.contains(relationship.getToMember().getId())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get all member IDs that user can edit
     * Returns null for SUPER_ADMIN (can edit all)
     * Returns set of IDs for BRANCH_ADMIN
     * Returns empty set for USER
     */
    @Transactional(readOnly = true)
    public Set<UUID> getEditableMemberIds(User user) {
        // SUPER_ADMIN can edit all - return null to bypass checks
        if (user.isSuperAdmin()) {
            return null;
        }

        Set<UUID> editableIds = new HashSet<>();

        // BRANCH_ADMIN: merge all subtrees
        if (user.isBranchAdmin()) {
            List<UUID> managedMemberIds = user.getManagedMemberIds();
            for (UUID managedMemberId : managedMemberIds) {
                editableIds.addAll(getSubtreeIds(managedMemberId));
            }
        }

        // USER role: return empty set
        return editableIds;
    }

    /**
     * Get all member IDs in a subtree (including root and spouses)
     * Uses BFS (Breadth-First Search) to traverse the tree
     */
    @Transactional(readOnly = true)
    public Set<UUID> getSubtreeIds(UUID rootMemberId) {
        log.debug("Getting subtree IDs for member: {}", rootMemberId);
        
        Set<UUID> result = new HashSet<>();
        Queue<UUID> queue = new LinkedList<>();
        queue.add(rootMemberId);

        while (!queue.isEmpty()) {
            UUID currentId = queue.poll();
            
            // Skip if already processed
            if (result.contains(currentId)) {
                continue;
            }

            result.add(currentId);

            // Find children (PARENT_CHILD relationships where current is parent)
            List<Relationship> childRelations = relationshipRepository
                    .findByFromMemberIdAndRelationshipType(currentId, RelationshipType.PARENT_CHILD);
            
            for (Relationship rel : childRelations) {
                UUID childId = rel.getToMember().getId();
                if (!result.contains(childId)) {
                    queue.add(childId);
                }
            }

            // Find spouses (SPOUSE relationships)
            List<Relationship> spouseRelations = relationshipRepository
                    .findByFromMemberIdAndRelationshipType(currentId, RelationshipType.SPOUSE);
            
            for (Relationship rel : spouseRelations) {
                UUID spouseId = rel.getToMember().getId();
                if (!result.contains(spouseId)) {
                    result.add(spouseId); // Add spouse but don't traverse their children
                }
            }
        }

        log.debug("Found {} members in subtree of {}", result.size(), rootMemberId);
        return result;
    }

    /**
     * Check if user can add a child to a parent member
     * (Used when creating new members with parent relationship)
     */
    @Transactional(readOnly = true)
    public boolean canAddChildToParent(User user, UUID parentId) {
        return canEditMember(user, parentId);
    }

    /**
     * Check if user can add a spouse to a member
     * BRANCH_ADMIN can add spouse if the member is in their subtree
     */
    @Transactional(readOnly = true)
    public boolean canAddSpouseToMember(User user, UUID memberId) {
        return canEditMember(user, memberId);
    }
}
