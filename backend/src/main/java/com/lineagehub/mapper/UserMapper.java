package com.lineagehub.mapper;

import com.lineagehub.dto.response.UserResponse;
import com.lineagehub.dto.response.UserSummaryResponse;
import com.lineagehub.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {UserRoleMapper.class})
public interface UserMapper {
    
    @Mapping(target = "roles", source = "roles")
    UserResponse toResponse(User user);
    
    UserSummaryResponse toSummaryResponse(User user);
}
