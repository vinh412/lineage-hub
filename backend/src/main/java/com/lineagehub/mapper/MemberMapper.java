package com.lineagehub.mapper;

import com.lineagehub.dto.request.CreateMemberRequest;
import com.lineagehub.dto.request.UpdateMemberRequest;
import com.lineagehub.dto.response.MemberDetailResponse;
import com.lineagehub.dto.response.MemberResponse;
import com.lineagehub.dto.response.MemberSummaryResponse;
import com.lineagehub.dto.response.UserSummaryResponse;
import com.lineagehub.entity.Member;
import com.lineagehub.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    
    @Mapping(target = "birthYear", source = "birthDate.year", defaultExpression = "java(null)")
    @Mapping(target = "deathYear", source = "deathDate.year", defaultExpression = "java(null)")
    @Mapping(target = "isDeceased", expression = "java(member.isDeceased())")
    @Mapping(target = "canEdit", ignore = true)  // Set manually in service
    @Mapping(target = "createdBy", source = "createdBy")
    MemberResponse toResponse(Member member);
    
    @Mapping(target = "birthYear", source = "birthDate.year", defaultExpression = "java(null)")
    @Mapping(target = "deathYear", source = "deathDate.year", defaultExpression = "java(null)")
    @Mapping(target = "isDeceased", expression = "java(member.isDeceased())")
    @Mapping(target = "canEdit", ignore = true)  // Set manually in service
    @Mapping(target = "relationships", ignore = true)  // Set manually in service
    @Mapping(target = "createdBy", source = "createdBy")
    MemberDetailResponse toDetailResponse(Member member);
    
    @Mapping(target = "birthYear", source = "birthDate.year", defaultExpression = "java(null)")
    @Mapping(target = "deathYear", source = "deathDate.year", defaultExpression = "java(null)")
    @Mapping(target = "isDeceased", expression = "java(member.isDeceased())")
    MemberSummaryResponse toSummaryResponse(Member member);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "relationshipsAsParent", ignore = true)
    @Mapping(target = "relationshipsAsChild", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    Member toEntity(CreateMemberRequest request);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "relationshipsAsParent", ignore = true)
    @Mapping(target = "relationshipsAsChild", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    void updateEntityFromRequest(UpdateMemberRequest request, @MappingTarget Member member);
    
    default UserSummaryResponse mapCreatedBy(User user) {
        if (user == null) {
            return null;
        }
        return UserSummaryResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .build();
    }
}
