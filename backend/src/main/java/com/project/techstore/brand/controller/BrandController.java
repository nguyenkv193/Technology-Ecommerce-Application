package com.project.techstore.brand.controller;

import com.project.techstore.brand.dto.BrandRequest;
import com.project.techstore.brand.dto.BrandResponse;
import com.project.techstore.brand.service.BrandService;
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
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
@Tag(name = "Brand Management", description = "Quản lý thương hiệu sản phẩm công nghệ")
public class BrandController {

    private final BrandService brandService;

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả các thương hiệu")
    public ResponseEntity<ApiResponse<List<BrandResponse>>> getAllBrands() {
        return ResponseEntity.ok(ApiResponse.success(brandService.getAllBrands()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Xem chi tiết thương hiệu theo ID")
    public ResponseEntity<ApiResponse<BrandResponse>> getBrandById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(brandService.getById(id)));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Xem chi tiết thương hiệu theo Slug URL")
    public ResponseEntity<ApiResponse<BrandResponse>> getBrandBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.success(brandService.getBySlug(slug)));
    }

    @PostMapping
    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Thêm thương hiệu mới (Admin)")
    public ResponseEntity<ApiResponse<BrandResponse>> createBrand(@Valid @RequestBody BrandRequest request) {
        BrandResponse created = brandService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Tạo thương hiệu thành công", created));
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật thông tin thương hiệu (Admin)")
    public ResponseEntity<ApiResponse<BrandResponse>> updateBrand(
            @PathVariable Long id,
            @Valid @RequestBody BrandRequest request
    ) {
        BrandResponse updated = brandService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thương hiệu thành công", updated));
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa thương hiệu (Admin)")
    public ResponseEntity<ApiResponse<Void>> deleteBrand(@PathVariable Long id) {
        brandService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa thương hiệu thành công", null));
    }
}
