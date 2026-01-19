package com.lineagehub.dto.request;

import com.lineagehub.entity.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddUserRoleRequest {
    
    @NotNull(message = "Role không được để trống")
    private Role role;
    
    private UUID managedMemberId;  // Required for BRANCH_ADMIN, must be null for others
}
