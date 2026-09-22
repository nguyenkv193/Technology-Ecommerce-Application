package com.project.techstore.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Tiêu chí lọc và tìm kiếm danh sách sản phẩm")
public class ProductFilterRequest {

    @Schema(description = "Từ khóa tìm kiếm theo tên hoặc mô tả", example = "MacBook Pro")
    private String keyword;

    @Schema(description = "Lọc theo ID danh mục", example = "1")
    private Long categoryId;

    @Schema(description = "Lọc theo ID thương hiệu", example = "2")
    private Long brandId;

    @Schema(description = "Giá tối thiểu", example = "10000000")
    private BigDecimal minPrice;

    @Schema(description = "Giá tối đa", example = "50000000")
    private BigDecimal maxPrice;

    @Schema(description = "Trạng thái sản phẩm (ACTIVE, INACTIVE, DRAFT)", example = "ACTIVE")
    private String status;

    @Schema(description = "Số trang (bắt đầu từ 0)", example = "0")
    @Builder.Default
    private Integer page = 0;

    @Schema(description = "Số phần tử trên một trang", example = "20")
    @Builder.Default
    private Integer size = 20;

    @Schema(description = "Trường cần sắp xếp (createdAt, name)", example = "createdAt")
    @Builder.Default
    private String sortBy = "createdAt";

    @Schema(description = "Chiều sắp xếp (ASC, DESC)", example = "DESC")
    @Builder.Default
    private String sortDirection = "DESC";
}
