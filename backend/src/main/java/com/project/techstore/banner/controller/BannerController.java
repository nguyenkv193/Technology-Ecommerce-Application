package com.project.techstore.banner.controller;

import com.project.techstore.banner.dto.BannerRequest;
import com.project.techstore.banner.dto.BannerResponse;
import com.project.techstore.banner.service.BannerService;
import com.project.techstore.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/banners")
@RequiredArgsConstructor
@Tag(name = "Banner Management", description = "Quản lý Banner quảng cáo & Hero Slider")
public class BannerController {

    private final BannerService bannerService;

    @GetMapping
    @Operation(summary = "Lấy danh sách Banner đang hoạt động theo vị trí (Public)")
    public ResponseEntity<ApiResponse<List<BannerResponse>>> getActiveBanners(
            @RequestParam(defaultValue = "HERO_SLIDER") String position
    ) {
        List<BannerResponse> banners = bannerService.getActiveBanners(position);
        return ResponseEntity.ok(ApiResponse.success(banners));
    }

    @GetMapping("/admin")
    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lấy toàn bộ Banner phục vụ trang quản trị Admin")
    public ResponseEntity<ApiResponse<List<BannerResponse>>> getAllBanners(
            @RequestParam(required = false) String position
    ) {
        List<BannerResponse> banners = bannerService.getAllBanners(position);
        return ResponseEntity.ok(ApiResponse.success(banners));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Xem chi tiết banner theo ID")
    public ResponseEntity<ApiResponse<BannerResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(bannerService.getById(id)));
    }

    @PostMapping
    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tạo mới banner (Admin)")
    public ResponseEntity<ApiResponse<BannerResponse>> createBanner(@Valid @RequestBody BannerRequest request) {
        BannerResponse created = bannerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Tạo banner thành công", created));
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật thông tin banner (Admin)")
    public ResponseEntity<ApiResponse<BannerResponse>> updateBanner(
            @PathVariable Long id,
            @Valid @RequestBody BannerRequest request
    ) {
        BannerResponse updated = bannerService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật banner thành công", updated));
    }

    @PatchMapping("/{id}/status")
    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Bật/Tắt trạng thái hoạt động banner (Admin)")
    public ResponseEntity<ApiResponse<Void>> toggleStatus(@PathVariable Long id) {
        bannerService.toggleStatus(id);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái thành công", null));
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa banner (Admin)")
    public ResponseEntity<ApiResponse<Void>> deleteBanner(@PathVariable Long id) {
        bannerService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa banner thành công", null));
    }

    @PostMapping("/{id}/click")
    @Operation(summary = "Ghi nhận lượt click vào banner (Public)")
    public ResponseEntity<ApiResponse<Void>> recordClick(@PathVariable Long id) {
        bannerService.recordClick(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
