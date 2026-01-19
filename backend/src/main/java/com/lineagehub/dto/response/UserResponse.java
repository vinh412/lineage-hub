package com.lineagehub.dto.response;

import com.lineagehub.entity.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private UUID id;
    private String email;
    private String fullName;
    private UserStatus status;
    private List<UserRoleResponse> roles;
    private Instant createdAt;
    private Instant updatedAt;
}
