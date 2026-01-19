package com.lineagehub.controller;

import com.lineagehub.dto.request.AddUserRoleRequest;
import com.lineagehub.dto.request.UpdateUserRolesRequest;
import com.lineagehub.dto.response.MessageResponse;
import com.lineagehub.dto.response.UserRoleResponse;
import com.lineagehub.security.CustomUserDetails;
import com.lineagehub.service.UserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users/{userId}/roles")
@RequiredArgsConstructor
@Tag(name = "User Roles", description = "User roles management endpoints (Super Admin only)")
@SecurityRequirement(name = "bearerAuth")
public class UserRoleController {

    private final UserRoleService userRoleService;

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Lấy danh sách roles của user", description = "Lấy tất cả roles của một user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "404", description = "User không tồn tại"),
            @ApiResponse(responseCode = "403", description = "Không có quyền")
    })
    public ResponseEntity<List<UserRoleResponse>> getUserRoles(
            @Parameter(description = "ID của user") @PathVariable UUID userId) {
        List<UserRoleResponse> roles = userRoleService.getUserRoles(userId);
        return ResponseEntity.ok(roles);
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Thêm role cho user", description = "Thêm một role mới cho user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Thêm role thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "404", description = "User hoặc Member không tồn tại"),
            @ApiResponse(responseCode = "409", description = "Role đã tồn tại"),
            @ApiResponse(responseCode = "403", description = "Không có quyền")
    })
    public ResponseEntity<UserRoleResponse> addUserRole(
            @Parameter(description = "ID của user") @PathVariable UUID userId,
            @Valid @RequestBody AddUserRoleRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        UserRoleResponse role = userRoleService.addUserRole(userId, request, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(role);
    }

    @PutMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Cập nhật tất cả roles của user", 
               description = "Thay thế toàn bộ roles của user bằng danh sách mới")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "404", description = "User hoặc Member không tồn tại"),
            @ApiResponse(responseCode = "403", description = "Không có quyền")
    })
    public ResponseEntity<List<UserRoleResponse>> updateUserRoles(
            @Parameter(description = "ID của user") @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserRolesRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        List<UserRoleResponse> roles = userRoleService.updateUserRoles(userId, request, currentUser.getId());
        return ResponseEntity.ok(roles);
    }

    @DeleteMapping("/{roleId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Xóa role của user", description = "Xóa một role cụ thể của user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Role không tồn tại"),
            @ApiResponse(responseCode = "409", description = "Không thể xóa role cuối cùng"),
            @ApiResponse(responseCode = "403", description = "Không có quyền")
    })
    public ResponseEntity<MessageResponse> deleteUserRole(
            @Parameter(description = "ID của user") @PathVariable UUID userId,
            @Parameter(description = "ID của role") @PathVariable UUID roleId) {
        userRoleService.deleteUserRole(userId, roleId);
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Đã xóa role thành công")
                .build());
    }
}
