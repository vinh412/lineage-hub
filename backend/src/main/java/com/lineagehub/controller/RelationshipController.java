package com.lineagehub.controller;

import com.lineagehub.dto.request.CreateParentChildRequest;
import com.lineagehub.dto.request.CreateSpouseRequest;
import com.lineagehub.dto.response.MessageResponse;
import com.lineagehub.dto.response.RelationshipResponse;
import com.lineagehub.security.CustomUserDetails;
import com.lineagehub.service.RelationshipService;
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
@RequestMapping("/api/relationships")
@RequiredArgsConstructor
@Tag(name = "Relationships", description = "Relationship management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class RelationshipController {

    private final RelationshipService relationshipService;

    @PostMapping("/parent-child")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'BRANCH_ADMIN')")
    @Operation(summary = "Tạo quan hệ cha-con", description = "Tạo quan hệ PARENT_CHILD giữa 2 members")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tạo thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ hoặc tạo vòng lặp"),
            @ApiResponse(responseCode = "404", description = "Member không tồn tại"),
            @ApiResponse(responseCode = "409", description = "Quan hệ đã tồn tại"),
            @ApiResponse(responseCode = "403", description = "Không có quyền")
    })
    public ResponseEntity<RelationshipResponse> createParentChildRelationship(
            @Valid @RequestBody CreateParentChildRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        RelationshipResponse response = relationshipService.createParentChildRelationship(
                request, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/spouse")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'BRANCH_ADMIN')")
    @Operation(summary = "Tạo quan hệ vợ-chồng", description = "Tạo quan hệ SPOUSE giữa 2 members (tự động 2 chiều)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tạo thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Member không tồn tại"),
            @ApiResponse(responseCode = "409", description = "Quan hệ đã tồn tại"),
            @ApiResponse(responseCode = "403", description = "Không có quyền")
    })
    public ResponseEntity<List<RelationshipResponse>> createSpouseRelationship(
            @Valid @RequestBody CreateSpouseRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        List<RelationshipResponse> responses = relationshipService.createSpouseRelationship(
                request, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'BRANCH_ADMIN')")
    @Operation(summary = "Xóa quan hệ", description = "Xóa một quan hệ (nếu là SPOUSE thì xóa cả 2 chiều)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Relationship không tồn tại"),
            @ApiResponse(responseCode = "403", description = "Không có quyền (không thể xóa quan hệ với đời trên)")
    })
    public ResponseEntity<MessageResponse> deleteRelationship(
            @Parameter(description = "ID của relationship") @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        relationshipService.deleteRelationship(id, currentUser.getId());
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Đã xóa quan hệ thành công")
                .build());
    }
}
