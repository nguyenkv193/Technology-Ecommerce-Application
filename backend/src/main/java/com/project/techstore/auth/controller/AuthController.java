package com.project.techstore.auth.controller;

import com.project.techstore.auth.dto.AuthResponse;
import com.project.techstore.auth.dto.LoginRequest;
import com.project.techstore.auth.dto.RefreshTokenRequest;
import com.project.techstore.auth.dto.RegisterRequest;
import com.project.techstore.auth.service.AuthService;
import com.project.techstore.common.response.ApiResponse;
import com.project.techstore.user.dto.UserResponse;
import com.project.techstore.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Các API Đăng ký, Đăng nhập, Làm mới Token và Xem thông tin tài khoản hiện tại")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "1. Đăng ký tài khoản mới", description = "Tạo tài khoản mới với vai trò USER, mật khẩu được mã hóa an toàn bằng BCrypt")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Đăng ký tài khoản thành công", response));
    }

    @PostMapping("/login")
    @Operation(summary = "2. Đăng nhập hệ thống", description = "Xác thực email và mật khẩu, trả về cặp JWT Access Token (30 phút) và Refresh Token (30 ngày)")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", response));
    }

    @PostMapping("/refresh")
    @Operation(summary = "3. Làm mới Access Token (Token Rotation)", description = "Gửi Refresh Token còn hạn để cấp lại Access Token mới và xoay vòng Refresh Token")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Làm mới Token thành công", response));
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "4. Lấy thông tin tài khoản đang đăng nhập", description = "Yêu cầu gắn Bearer JWT Token ở Header")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(authService.getCurrentUser(currentUser)));
    }
}
