package com.project.techstore.product.controller;

import com.project.techstore.common.response.ApiResponse;
import com.project.techstore.product.dto.ProductStorefrontContentDto;
import com.project.techstore.product.service.ProductStorefrontContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products/{productId}/storefront")
@RequiredArgsConstructor
public class ProductStorefrontContentController {

    private final ProductStorefrontContentService contentService;

    @GetMapping
    @Operation(summary = "Lấy ưu đãi, FAQ, media và chính sách đã cấu hình cho sản phẩm")
    public ResponseEntity<ApiResponse<ProductStorefrontContentDto>> get(@PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(contentService.get(productId)));
    }

    @PutMapping
    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật nội dung trang chi tiết sản phẩm (Admin)")
    public ResponseEntity<ApiResponse<ProductStorefrontContentDto>> update(
            @PathVariable Long productId,
            @Valid @RequestBody ProductStorefrontContentDto request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Cập nhật nội dung sản phẩm thành công", contentService.update(productId, request)));
    }
}
