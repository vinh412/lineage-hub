package com.lineagehub.controller;

import com.lineagehub.dto.request.LoginRequest;
import com.lineagehub.dto.request.RegisterRequest;
import com.lineagehub.dto.response.AuthResponse;
import com.lineagehub.dto.response.UserResponse;
import com.lineagehub.security.CustomUserDetails;
import com.lineagehub.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication endpoints for login and registration")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Đăng ký tài khoản mới", description = "Tạo tài khoản mới, cần admin phê duyệt")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Đăng ký thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "409", description = "Email đã tồn tại")
    })
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Đăng nhập", description = "Đăng nhập và nhận JWT token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Đăng nhập thành công"),
            @ApiResponse(responseCode = "401", description = "Email hoặc mật khẩu không đúng"),
            @ApiResponse(responseCode = "403", description = "Tài khoản chưa được phê duyệt")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(summary = "Lấy thông tin user hiện tại", description = "Lấy thông tin user đang đăng nhập")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "401", description = "Chưa đăng nhập")
    })
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserResponse response = authService.getCurrentUser(userDetails.getId());
        return ResponseEntity.ok(response);
    }
}
