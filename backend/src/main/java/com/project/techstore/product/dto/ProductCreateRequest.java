package com.project.techstore.product.dto;

import com.project.techstore.product.entity.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Yêu cầu tạo mới sản phẩm công nghệ")
public class ProductCreateRequest {

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Schema(description = "Tên sản phẩm", example = "iPhone 16 Pro Max")
    private String name;

    @Schema(description = "Slug URL tuỳ chỉnh (nếu để trống hệ thống tự sinh)", example = "iphone-16-pro-max")
    private String slug;

    @Schema(description = "Mô tả chi tiết sản phẩm", example = "Điện thoại Apple cao cấp nhất với chip Apple A18 Pro...")
    private String description;

    @NotNull(message = "Danh mục sản phẩm không được để trống")
    @Schema(description = "ID danh mục", example = "1")
    private Long categoryId;

    @NotNull(message = "Thương hiệu sản phẩm không được để trống")
    @Schema(description = "ID thương hiệu", example = "1")
    private Long brandId;

    @Schema(description = "Trạng thái sản phẩm", example = "ACTIVE")
    @Builder.Default
    private ProductStatus status = ProductStatus.ACTIVE;

    @Valid
    @Schema(description = "Danh sách thông số kỹ thuật (CPU, RAM, GPU, Màn hình,...)")
    @Builder.Default
    private List<ProductAttributeDto> attributes = new ArrayList<>();

    @Valid
    @NotEmpty(message = "Sản phẩm phải có ít nhất một phiên bản cấu hình / SKU")
    @Schema(description = "Danh sách phiên bản cấu hình và giá bán")
    @Builder.Default
    private List<ProductVariantDto> variants = new ArrayList<>();

    @Valid
    @Schema(description = "Danh sách hình ảnh sản phẩm")
    @Builder.Default
    private List<ProductImageDto> images = new ArrayList<>();
}
