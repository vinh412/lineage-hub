package com.lineagehub.controller;

import com.lineagehub.dto.response.MessageResponse;
import com.lineagehub.dto.response.UserResponse;
import com.lineagehub.entity.enums.UserStatus;
import com.lineagehub.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "User management endpoints (Super Admin only)")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Lấy danh sách users", description = "Lấy danh sách tất cả users với phân trang và filter")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "403", description = "Không có quyền")
    })
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @Parameter(description = "Trạng thái user")
            @RequestParam(required = false) UserStatus status,
            @Parameter(description = "Tìm kiếm theo tên hoặc email")
            @RequestParam(required = false, defaultValue = "") String search,
            @Parameter(description = "Số trang (0-indexed)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Số items trên mỗi trang")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sắp xếp theo field")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Hướng sắp xếp (ASC, DESC)")
            @RequestParam(defaultValue = "DESC") String sortDir) {
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<UserResponse> users = userService.getAllUsers(status, search, pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Lấy chi tiết user", description = "Lấy thông tin chi tiết của một user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "404", description = "User không tồn tại"),
            @ApiResponse(responseCode = "403", description = "Không có quyền")
    })
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "ID của user") @PathVariable UUID id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Phê duyệt user", description = "Phê duyệt user từ PENDING sang ACTIVE")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Phê duyệt thành công"),
            @ApiResponse(responseCode = "404", description = "User không tồn tại"),
            @ApiResponse(responseCode = "403", description = "Không có quyền")
    })
    public ResponseEntity<UserResponse> approveUser(
            @Parameter(description = "ID của user") @PathVariable UUID id) {
        UserResponse user = userService.approveUser(id);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Vô hiệu hóa user", description = "Chuyển user sang trạng thái INACTIVE")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vô hiệu hóa thành công"),
            @ApiResponse(responseCode = "404", description = "User không tồn tại"),
            @ApiResponse(responseCode = "403", description = "Không có quyền")
    })
    public ResponseEntity<UserResponse> deactivateUser(
            @Parameter(description = "ID của user") @PathVariable UUID id) {
        UserResponse user = userService.deactivateUser(id);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Kích hoạt user", description = "Chuyển user sang trạng thái ACTIVE")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Kích hoạt thành công"),
            @ApiResponse(responseCode = "404", description = "User không tồn tại"),
            @ApiResponse(responseCode = "403", description = "Không có quyền")
    })
    public ResponseEntity<UserResponse> activateUser(
            @Parameter(description = "ID của user") @PathVariable UUID id) {
        UserResponse user = userService.activateUser(id);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Xóa user", description = "Xóa user khỏi hệ thống")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "User không tồn tại"),
            @ApiResponse(responseCode = "403", description = "Không có quyền")
    })
    public ResponseEntity<MessageResponse> deleteUser(
            @Parameter(description = "ID của user") @PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Đã xóa user thành công")
                .build());
    }
}
