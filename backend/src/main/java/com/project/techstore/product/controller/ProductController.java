package com.project.techstore.product.controller;

import com.project.techstore.common.dto.PageResponse;
import com.project.techstore.common.response.ApiResponse;
import com.project.techstore.product.dto.ProductCreateRequest;
import com.project.techstore.product.dto.ProductDetailResponse;
import com.project.techstore.product.dto.ProductFilterRequest;
import com.project.techstore.product.dto.ProductResponse;
import com.project.techstore.product.dto.ProductUpdateRequest;
import com.project.techstore.product.dto.ProductRecommendationFeature;
import com.project.techstore.recommendation.service.RecommendationService;
import com.project.techstore.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import java.util.List;

@RestController
@Validated
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product Management", description = "Quản lý và tìm kiếm danh mục sản phẩm công nghệ")
public class ProductController {

    private final ProductService productService;
    private final RecommendationService recommendationService;

    @GetMapping("/best-sellers")
    @Operation(summary = "Sản phẩm bán chạy từ các đơn đã giao và thanh toán thành công")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> bestSellers(
            @RequestParam(defaultValue = "8") @Min(1) @Max(50) int limit) {
        return ResponseEntity.ok(ApiResponse.success(recommendationService.getBestSellers(limit)));
    }

    @GetMapping("/recommendation-features")
    @Operation(summary = "Danh mục và thông số kỹ thuật để xây dựng mô hình Content-Based")
    public ResponseEntity<ApiResponse<PageResponse<ProductRecommendationFeature>>> recommendationFeatures(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "100") @Min(1) @Max(500) int size) {
        return ResponseEntity.ok(ApiResponse.success(productService.getRecommendationFeatures(page, size)));
    }

    @GetMapping
    @Operation(summary = "Tìm kiếm & lọc danh sách sản phẩm (kèm phân trang, lọc theo danh mục, thương hiệu, khoảng giá)")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> filterProducts(
            @ParameterObject ProductFilterRequest filter
    ) {
        PageResponse<ProductResponse> response = productService.filterProducts(filter);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Xem chi tiết sản phẩm theo ID (kèm biến thể SKU, thông số kỹ thuật, hình ảnh)")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(productService.getById(id)));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Xem chi tiết sản phẩm theo Slug URL")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> getProductBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.success(productService.getBySlug(slug)));
    }

    @PostMapping
    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tạo mới sản phẩm công nghệ (Admin)")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> createProduct(
            @Valid @RequestBody ProductCreateRequest request
    ) {
        ProductDetailResponse created = productService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Tạo sản phẩm thành công", created));
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật sản phẩm công nghệ (Admin)")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest request
    ) {
        ProductDetailResponse updated = productService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật sản phẩm thành công", updated));
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa sản phẩm công nghệ (Admin)")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa sản phẩm thành công", null));
    }
}
