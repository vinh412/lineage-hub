package com.lineagehub.controller;

import com.lineagehub.dto.request.CreateMemberRequest;
import com.lineagehub.dto.request.UpdateMemberRequest;
import com.lineagehub.dto.response.MemberDetailResponse;
import com.lineagehub.dto.response.MemberResponse;
import com.lineagehub.dto.response.MessageResponse;
import com.lineagehub.entity.enums.Gender;
import com.lineagehub.security.CustomUserDetails;
import com.lineagehub.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "Members", description = "Member management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    @Operation(summary = "Lấy danh sách thành viên", description = "Lấy danh sách thành viên với phân trang và filter")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "401", description = "Chưa đăng nhập")
    })
    public ResponseEntity<Page<MemberResponse>> getMembers(
            @Parameter(description = "Tìm kiếm theo tên")
            @RequestParam(required = false) String search,
            @Parameter(description = "Filter theo đời")
            @RequestParam(required = false) Integer generation,
            @Parameter(description = "Filter theo giới tính")
            @RequestParam(required = false) Gender gender,
            @Parameter(description = "Filter theo con ruột/dâu rể")
            @RequestParam(required = false) Boolean isBloodRelative,
            @Parameter(description = "Filter theo còn sống/đã mất")
            @RequestParam(required = false) Boolean isDeceased,
            @Parameter(description = "Số trang (0-indexed)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Số items trên mỗi trang")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sắp xếp theo field")
            @RequestParam(defaultValue = "fullName") String sortBy,
            @Parameter(description = "Hướng sắp xếp (ASC, DESC)")
            @RequestParam(defaultValue = "ASC") String sortDir,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<MemberResponse> members = memberService.getMembers(
                search, generation, gender, isBloodRelative, isDeceased, pageable, currentUser.getId());
        
        return ResponseEntity.ok(members);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết thành viên", description = "Lấy thông tin chi tiết của một thành viên")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "404", description = "Member không tồn tại"),
            @ApiResponse(responseCode = "401", description = "Chưa đăng nhập")
    })
    public ResponseEntity<MemberDetailResponse> getMemberById(
            @Parameter(description = "ID của member") @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        MemberDetailResponse member = memberService.getMemberById(id, currentUser.getId());
        return ResponseEntity.ok(member);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'BRANCH_ADMIN')")
    @Operation(summary = "Tạo thành viên mới", description = "Tạo một thành viên mới trong gia phả")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tạo thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "403", description = "Không có quyền"),
            @ApiResponse(responseCode = "401", description = "Chưa đăng nhập")
    })
    public ResponseEntity<MemberResponse> createMember(
            @Valid @RequestBody CreateMemberRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        MemberResponse member = memberService.createMember(request, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(member);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'BRANCH_ADMIN')")
    @Operation(summary = "Cập nhật thành viên", description = "Cập nhật thông tin của một thành viên")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Member không tồn tại"),
            @ApiResponse(responseCode = "403", description = "Không có quyền"),
            @ApiResponse(responseCode = "401", description = "Chưa đăng nhập")
    })
    public ResponseEntity<MemberResponse> updateMember(
            @Parameter(description = "ID của member") @PathVariable UUID id,
            @Valid @RequestBody UpdateMemberRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        MemberResponse member = memberService.updateMember(id, request, currentUser.getId());
        return ResponseEntity.ok(member);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'BRANCH_ADMIN')")
    @Operation(summary = "Xóa thành viên", description = "Xóa một thành viên khỏi gia phả")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Member không tồn tại"),
            @ApiResponse(responseCode = "409", description = "Member có quan hệ, không thể xóa"),
            @ApiResponse(responseCode = "403", description = "Không có quyền"),
            @ApiResponse(responseCode = "401", description = "Chưa đăng nhập")
    })
    public ResponseEntity<MessageResponse> deleteMember(
            @Parameter(description = "ID của member") @PathVariable UUID id,
            @Parameter(description = "Force delete kèm relationships")
            @RequestParam(defaultValue = "false") boolean force,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        memberService.deleteMember(id, currentUser.getId(), force);
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Đã xóa member thành công")
                .build());
    }
}
