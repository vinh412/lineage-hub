package com.lineagehub.mapper;

import com.lineagehub.dto.response.RelationshipResponse;
import com.lineagehub.dto.response.UserSummaryResponse;
import com.lineagehub.entity.Relationship;
import com.lineagehub.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RelationshipMapper {
    
    @Mapping(target = "fromMemberId", source = "fromMember.id")
    @Mapping(target = "fromMemberName", source = "fromMember.fullName")
    @Mapping(target = "toMemberId", source = "toMember.id")
    @Mapping(target = "toMemberName", source = "toMember.fullName")
    @Mapping(target = "canEdit", ignore = true)  // Set manually in service
    @Mapping(target = "createdBy", source = "createdBy")
    RelationshipResponse toResponse(Relationship relationship);
    
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
