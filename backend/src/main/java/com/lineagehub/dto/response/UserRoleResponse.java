package com.lineagehub.dto.response;

import com.lineagehub.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleResponse {
    private UUID id;
    private Role role;
    private UUID managedMemberId;
    private String managedMemberName;
    private Integer managedMemberGeneration;
    private Instant createdAt;
    private UserSummaryResponse createdBy;
}
