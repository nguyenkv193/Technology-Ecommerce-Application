package com.project.techstore.user.controller;

import com.project.techstore.common.response.ApiResponse;
import com.project.techstore.user.dto.*;
import com.project.techstore.user.entity.User;
import com.project.techstore.user.service.AddressService;
import com.project.techstore.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "User Management", description = "Quản lý thông tin tài khoản cá nhân và sổ địa chỉ giao hàng")
public class UserController {

    private final UserService userService;
    private final AddressService addressService;

    // ==========================================
    // USER PROFILE
    // ==========================================

    @GetMapping("/profile")
    @Operation(summary = "Xem thông tin cá nhân của người dùng hiện tại")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(userService.getProfile(currentUser.getId())));
    }

    @PutMapping("/profile")
    @Operation(summary = "Cập nhật thông tin cá nhân (Họ tên, số điện thoại)")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody UserProfileUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thông tin thành công", userService.updateProfile(currentUser.getId(), request)));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Đổi mật khẩu tài khoản")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        userService.changePassword(currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Đổi mật khẩu thành công", null));
    }

    // ==========================================
    // ADDRESS BOOK
    // ==========================================

    @GetMapping("/addresses")
    @Operation(summary = "Lấy danh sách sổ địa chỉ của người dùng")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAddresses(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(addressService.getUserAddresses(currentUser.getId())));
    }

    @PostMapping("/addresses")
    @Operation(summary = "Thêm địa chỉ giao hàng mới")
    public ResponseEntity<ApiResponse<AddressResponse>> createAddress(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody AddressRequest request
    ) {
        AddressResponse created = addressService.createAddress(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Đã thêm địa chỉ mới", created));
    }

    @PutMapping("/addresses/{id}")
    @Operation(summary = "Cập nhật thông tin địa chỉ giao hàng")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest request
    ) {
        AddressResponse updated = addressService.updateAddress(currentUser.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Đã cập nhật địa chỉ", updated));
    }

    @DeleteMapping("/addresses/{id}")
    @Operation(summary = "Xóa địa chỉ giao hàng")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id
    ) {
        addressService.deleteAddress(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa địa chỉ thành công", null));
    }

    @PatchMapping("/addresses/{id}/default")
    @Operation(summary = "Thiết lập địa chỉ mặc định")
    public ResponseEntity<ApiResponse<AddressResponse>> setDefaultAddress(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id
    ) {
        AddressResponse defaultAddress = addressService.setDefaultAddress(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Đã đặt làm địa chỉ mặc định", defaultAddress));
    }
}
