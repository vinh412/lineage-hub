package com.lineagehub.service;

import com.lineagehub.dto.request.CreateParentChildRequest;
import com.lineagehub.dto.request.CreateSpouseRequest;
import com.lineagehub.dto.response.RelationshipResponse;
import com.lineagehub.entity.Member;
import com.lineagehub.entity.Relationship;
import com.lineagehub.entity.User;
import com.lineagehub.entity.enums.RelationshipType;
import com.lineagehub.exception.BusinessException;
import com.lineagehub.exception.ResourceNotFoundException;
import com.lineagehub.exception.UnauthorizedException;
import com.lineagehub.mapper.RelationshipMapper;
import com.lineagehub.repository.MemberRepository;
import com.lineagehub.repository.RelationshipRepository;
import com.lineagehub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RelationshipService {

    private final RelationshipRepository relationshipRepository;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final RelationshipMapper relationshipMapper;
    private final AuthorizationService authorizationService;

    @Transactional
    public RelationshipResponse createParentChildRelationship(CreateParentChildRequest request, UUID currentUserId) {
        log.info("Creating parent-child relationship: parent={}, child={}", request.getParentId(), request.getChildId());
        
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", currentUserId));
        
        // Validate members exist
        Member parent = memberRepository.findById(request.getParentId())
                .orElseThrow(() -> new ResourceNotFoundException("Parent member", request.getParentId()));
        
        Member child = memberRepository.findById(request.getChildId())
                .orElseThrow(() -> new ResourceNotFoundException("Child member", request.getChildId()));
        
        // Check authorization
        if (!authorizationService.canAddChildToParent(currentUser, request.getParentId())) {
            throw new UnauthorizedException("Bạn không có quyền tạo quan hệ này");
        }
        
        // Validate not creating relationship with self
        if (request.getParentId().equals(request.getChildId())) {
            throw new BusinessException("Không thể tạo quan hệ với chính mình");
        }
        
        // Check if relationship already exists
        if (relationshipRepository.existsByFromMemberIdAndToMemberIdAndRelationshipType(
                request.getParentId(), request.getChildId(), RelationshipType.PARENT_CHILD)) {
            throw new BusinessException("Quan hệ cha-con đã tồn tại");
        }
        
        // Check cycle detection (child cannot be ancestor of parent)
        if (wouldCreateCycle(request.getChildId(), request.getParentId())) {
            throw new BusinessException("Không thể tạo quan hệ này vì sẽ tạo vòng lặp trong cây gia phả");
        }
        
        // Check child doesn't have more than 2 parents
        List<Relationship> existingParents = relationshipRepository.findParentsByMemberId(request.getChildId());
        if (existingParents.size() >= 2) {
            throw new BusinessException("Mỗi thành viên chỉ có tối đa 2 cha mẹ");
        }
        
        // Create relationship
        Relationship relationship = Relationship.builder()
                .fromMember(parent)
                .toMember(child)
                .relationshipType(RelationshipType.PARENT_CHILD)
                .createdBy(currentUser)
                .build();
        
        Relationship savedRelationship = relationshipRepository.save(relationship);
        log.info("Parent-child relationship created successfully");
        
        RelationshipResponse response = relationshipMapper.toResponse(savedRelationship);
        response.setCanEdit(true);
        return response;
    }

    @Transactional
    public List<RelationshipResponse> createSpouseRelationship(CreateSpouseRequest request, UUID currentUserId) {
        log.info("Creating spouse relationship: member1={}, member2={}", request.getMember1Id(), request.getMember2Id());
        
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", currentUserId));
        
        // Validate members exist
        Member member1 = memberRepository.findById(request.getMember1Id())
                .orElseThrow(() -> new ResourceNotFoundException("Member 1", request.getMember1Id()));
        
        Member member2 = memberRepository.findById(request.getMember2Id())
                .orElseThrow(() -> new ResourceNotFoundException("Member 2", request.getMember2Id()));
        
        // Check authorization (can add spouse if at least one member is in user's subtree)
        if (!authorizationService.canAddSpouseToMember(currentUser, request.getMember1Id())
                && !authorizationService.canAddSpouseToMember(currentUser, request.getMember2Id())) {
            throw new UnauthorizedException("Bạn không có quyền tạo quan hệ này");
        }
        
        // Validate not creating relationship with self
        if (request.getMember1Id().equals(request.getMember2Id())) {
            throw new BusinessException("Không thể tạo quan hệ với chính mình");
        }
        
        // Check if relationship already exists
        if (relationshipRepository.existsByFromMemberIdAndToMemberIdAndRelationshipType(
                request.getMember1Id(), request.getMember2Id(), RelationshipType.SPOUSE)) {
            throw new BusinessException("Quan hệ vợ-chồng đã tồn tại");
        }
        
        // Create both directions for spouse relationship
        Relationship rel1 = Relationship.builder()
                .fromMember(member1)
                .toMember(member2)
                .relationshipType(RelationshipType.SPOUSE)
                .createdBy(currentUser)
                .build();
        
        Relationship rel2 = Relationship.builder()
                .fromMember(member2)
                .toMember(member1)
                .relationshipType(RelationshipType.SPOUSE)
                .createdBy(currentUser)
                .build();
        
        Relationship savedRel1 = relationshipRepository.save(rel1);
        Relationship savedRel2 = relationshipRepository.save(rel2);
        
        log.info("Spouse relationship created successfully (bidirectional)");
        
        List<RelationshipResponse> responses = new ArrayList<>();
        RelationshipResponse response1 = relationshipMapper.toResponse(savedRel1);
        response1.setCanEdit(true);
        responses.add(response1);
        
        RelationshipResponse response2 = relationshipMapper.toResponse(savedRel2);
        response2.setCanEdit(true);
        responses.add(response2);
        
        return responses;
    }

    @Transactional
    public void deleteRelationship(UUID id, UUID currentUserId) {
        log.info("Deleting relationship: {}", id);
        
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", currentUserId));
        
        Relationship relationship = relationshipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Relationship", id));
        
        // Check authorization
        if (!authorizationService.canEditRelationship(currentUser, relationship)) {
            throw new UnauthorizedException("Bạn không có quyền xóa quan hệ này");
        }
        
        // If it's a spouse relationship, delete both directions
        if (relationship.getRelationshipType() == RelationshipType.SPOUSE) {
            // Find and delete the reverse relationship
            List<Relationship> reverseRelationships = relationshipRepository
                    .findByFromMemberIdAndRelationshipType(
                            relationship.getToMember().getId(), RelationshipType.SPOUSE);
            
            for (Relationship reverse : reverseRelationships) {
                if (reverse.getToMember().getId().equals(relationship.getFromMember().getId())) {
                    relationshipRepository.delete(reverse);
                    break;
                }
            }
        }
        
        relationshipRepository.delete(relationship);
        log.info("Relationship deleted successfully");
    }

    /**
     * Check if adding parent->child would create a cycle
     * (i.e., child is an ancestor of parent)
     */
    private boolean wouldCreateCycle(UUID childId, UUID parentId) {
        Set<UUID> visited = new HashSet<>();
        Queue<UUID> queue = new LinkedList<>();
        queue.add(childId);
        
        while (!queue.isEmpty()) {
            UUID currentId = queue.poll();
            
            if (visited.contains(currentId)) {
                continue;
            }
            
            visited.add(currentId);
            
            // If we reach the parent while traversing child's children, it's a cycle
            if (currentId.equals(parentId)) {
                return true;
            }
            
            // Add children to queue
            List<Relationship> childRelationships = relationshipRepository
                    .findByFromMemberIdAndRelationshipType(currentId, RelationshipType.PARENT_CHILD);
            
            for (Relationship rel : childRelationships) {
                queue.add(rel.getToMember().getId());
            }
        }
        
        return false;
    }
}
