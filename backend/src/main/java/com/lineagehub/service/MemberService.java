package com.lineagehub.service;

import com.lineagehub.dto.request.CreateMemberRequest;
import com.lineagehub.dto.request.UpdateMemberRequest;
import com.lineagehub.dto.response.*;
import com.lineagehub.entity.Member;
import com.lineagehub.entity.Relationship;
import com.lineagehub.entity.User;
import com.lineagehub.entity.enums.Gender;
import com.lineagehub.entity.enums.RelationshipType;
import com.lineagehub.exception.BusinessException;
import com.lineagehub.exception.ResourceNotFoundException;
import com.lineagehub.exception.UnauthorizedException;
import com.lineagehub.mapper.MemberMapper;
import com.lineagehub.repository.MemberRepository;
import com.lineagehub.repository.RelationshipRepository;
import com.lineagehub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final RelationshipRepository relationshipRepository;
    private final UserRepository userRepository;
    private final MemberMapper memberMapper;
    private final AuthorizationService authorizationService;

    @Transactional(readOnly = true)
    public Page<MemberResponse> getMembers(String search, Integer generation, Gender gender, 
                                           Boolean isBloodRelative, Boolean isDeceased, 
                                           Pageable pageable, UUID currentUserId) {
        log.info("Fetching members with filters");
        
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", currentUserId));
        
        Page<Member> members = memberRepository.findByFilters(
                search, generation, gender, isBloodRelative, isDeceased, pageable);
        
        return members.map(member -> {
            MemberResponse response = memberMapper.toResponse(member);
            response.setCanEdit(authorizationService.canEditMember(currentUser, member.getId()));
            return response;
        });
    }

    @Transactional(readOnly = true)
    public MemberDetailResponse getMemberById(UUID id, UUID currentUserId) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", id));
        
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", currentUserId));
        
        MemberDetailResponse response = memberMapper.toDetailResponse(member);
        response.setCanEdit(authorizationService.canEditMember(currentUser, member.getId()));
        
        // Load relationships
        MemberRelationshipsResponse relationships = loadMemberRelationships(id);
        response.setRelationships(relationships);
        
        return response;
    }

    @Transactional
    public MemberResponse createMember(CreateMemberRequest request, UUID currentUserId) {
        log.info("Creating new member: {}", request.getFullName());
        
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", currentUserId));
        
        // Validate dates
        validateDates(request.getBirthDate(), request.getDeathDate());
        
        // Validate parent count
        if (request.getParentIds() != null && request.getParentIds().size() > 2) {
            throw new BusinessException("Mỗi thành viên chỉ có tối đa 2 cha mẹ");
        }
        
        // Check authorization for parents (if adding child to existing members)
        if (request.getParentIds() != null && !request.getParentIds().isEmpty()) {
            for (UUID parentId : request.getParentIds()) {
                if (!authorizationService.canAddChildToParent(currentUser, parentId)) {
                    throw new UnauthorizedException("Bạn không có quyền thêm con cho member này");
                }
            }
        }
        
        // Create member entity
        Member member = memberMapper.toEntity(request);
        member.setCreatedBy(currentUser);
        
        Member savedMember = memberRepository.save(member);
        
        // Create parent relationships
        if (request.getParentIds() != null) {
            for (UUID parentId : request.getParentIds()) {
                Member parent = memberRepository.findById(parentId)
                        .orElseThrow(() -> new ResourceNotFoundException("Parent member", parentId));
                
                Relationship relationship = Relationship.builder()
                        .fromMember(parent)
                        .toMember(savedMember)
                        .relationshipType(RelationshipType.PARENT_CHILD)
                        .createdBy(currentUser)
                        .build();
                
                relationshipRepository.save(relationship);
            }
        }
        
        // Create spouse relationships (bidirectional)
        if (request.getSpouseIds() != null) {
            for (UUID spouseId : request.getSpouseIds()) {
                if (!authorizationService.canAddSpouseToMember(currentUser, spouseId)) {
                    throw new UnauthorizedException("Bạn không có quyền thêm vợ/chồng cho member này");
                }
                
                Member spouse = memberRepository.findById(spouseId)
                        .orElseThrow(() -> new ResourceNotFoundException("Spouse member", spouseId));
                
                // Create both directions
                Relationship rel1 = Relationship.builder()
                        .fromMember(savedMember)
                        .toMember(spouse)
                        .relationshipType(RelationshipType.SPOUSE)
                        .createdBy(currentUser)
                        .build();
                
                Relationship rel2 = Relationship.builder()
                        .fromMember(spouse)
                        .toMember(savedMember)
                        .relationshipType(RelationshipType.SPOUSE)
                        .createdBy(currentUser)
                        .build();
                
                relationshipRepository.save(rel1);
                relationshipRepository.save(rel2);
            }
        }
        
        log.info("Member created successfully with ID: {}", savedMember.getId());
        
        MemberResponse response = memberMapper.toResponse(savedMember);
        response.setCanEdit(true); // Creator can always edit
        return response;
    }

    @Transactional
    public MemberResponse updateMember(UUID id, UpdateMemberRequest request, UUID currentUserId) {
        log.info("Updating member: {}", id);
        
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", currentUserId));
        
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", id));
        
        // Check authorization
        if (!authorizationService.canEditMember(currentUser, id)) {
            throw new UnauthorizedException("Bạn không có quyền sửa member này");
        }
        
        // Validate dates
        validateDates(request.getBirthDate(), request.getDeathDate());
        
        // Update member fields
        memberMapper.updateEntityFromRequest(request, member);
        
        Member savedMember = memberRepository.save(member);
        log.info("Member updated successfully: {}", id);
        
        MemberResponse response = memberMapper.toResponse(savedMember);
        response.setCanEdit(true);
        return response;
    }

    @Transactional
    public void deleteMember(UUID id, UUID currentUserId, boolean force) {
        log.info("Deleting member: {}", id);
        
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", currentUserId));
        
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", id));
        
        // Check authorization
        if (!authorizationService.canEditMember(currentUser, id)) {
            throw new UnauthorizedException("Bạn không có quyền xóa member này");
        }
        
        // Check if member has relationships
        boolean hasRelationships = relationshipRepository.existsByFromMemberIdOrToMemberId(id, id);
        
        if (hasRelationships && !force) {
            throw new BusinessException("Không thể xóa member có quan hệ gia đình. Sử dụng force=true để xóa kèm quan hệ");
        }
        
        // Only SUPER_ADMIN can force delete
        if (force && !currentUser.isSuperAdmin()) {
            throw new UnauthorizedException("Chỉ Super Admin mới có thể force delete");
        }
        
        memberRepository.delete(member);
        log.info("Member deleted successfully: {}", id);
    }

    private MemberRelationshipsResponse loadMemberRelationships(UUID memberId) {
        // Get parents
        List<Relationship> parentRelations = relationshipRepository.findParentsByMemberId(memberId);
        List<MemberSummaryResponse> parents = parentRelations.stream()
                .map(rel -> memberMapper.toSummaryResponse(rel.getFromMember()))
                .collect(Collectors.toList());
        
        // Get spouses
        List<Relationship> spouseRelations = relationshipRepository.findSpousesByMemberId(memberId);
        List<MemberSummaryResponse> spouses = spouseRelations.stream()
                .map(rel -> {
                    Member spouse = rel.getFromMember().getId().equals(memberId) 
                            ? rel.getToMember() 
                            : rel.getFromMember();
                    return memberMapper.toSummaryResponse(spouse);
                })
                .distinct()
                .collect(Collectors.toList());
        
        // Get children
        List<Relationship> childRelations = relationshipRepository.findChildrenByMemberId(memberId);
        List<MemberSummaryResponse> children = childRelations.stream()
                .map(rel -> memberMapper.toSummaryResponse(rel.getToMember()))
                .collect(Collectors.toList());
        
        return MemberRelationshipsResponse.builder()
                .parents(parents)
                .spouses(spouses)
                .children(children)
                .build();
    }

    private void validateDates(LocalDate birthDate, LocalDate deathDate) {
        if (birthDate != null && deathDate != null && deathDate.isBefore(birthDate)) {
            throw new BusinessException("Ngày mất không thể trước ngày sinh");
        }
    }
}
