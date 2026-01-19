package com.lineagehub.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRolesRequest {
    
    @NotEmpty(message = "Danh sách roles không được rỗng")
    @Valid
    private List<AddUserRoleRequest> roles;
}
