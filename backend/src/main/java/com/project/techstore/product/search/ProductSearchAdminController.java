package com.project.techstore.product.search;

import com.project.techstore.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/search")
public class ProductSearchAdminController {

    private final ProductSearchIndexService indexService;

    @PostMapping("/reindex-products")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Tạo lại toàn bộ chỉ mục tìm kiếm sản phẩm từ PostgreSQL (Admin)")
    public ResponseEntity<ApiResponse<Long>> reindexProducts() {
        long indexedProducts = indexService.reindexAllProducts();
        return ResponseEntity.ok(ApiResponse.success(
                "Đã tạo lại chỉ mục tìm kiếm sản phẩm",
                indexedProducts
        ));
    }
}
