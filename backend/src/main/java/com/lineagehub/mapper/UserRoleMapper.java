package com.lineagehub.mapper;

import com.lineagehub.dto.response.UserRoleResponse;
import com.lineagehub.entity.UserRole;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserRoleMapper {
    
    @Mapping(target = "managedMemberId", source = "managedMember.id")
    @Mapping(target = "managedMemberName", source = "managedMember.fullName")
    @Mapping(target = "managedMemberGeneration", source = "managedMember.generation")
    @Mapping(target = "createdBy", source = "createdBy")
    UserRoleResponse toResponse(UserRole userRole);
    
    @Named("toSummaryCreatedBy")
    default com.lineagehub.dto.response.UserSummaryResponse mapCreatedBy(com.lineagehub.entity.User user) {
        if (user == null) {
            return null;
        }
        return com.lineagehub.dto.response.UserSummaryResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .build();
    }
}
