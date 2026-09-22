package com.project.techstore.product.dto;

import com.project.techstore.product.entity.ProductVariant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Phiên bản cấu hình / SKU của sản phẩm")
public class ProductVariantDto {

    private Long id;

    @Schema(description = "Mã SKU định danh phân loại", example = "IP16PM-256GB-DESERT")
    private String sku;

    @NotBlank(message = "Tên phiên bản cấu hình không được để trống")
    @Schema(description = "Tên biến thể cấu hình", example = "256GB - Titan Sa Mạc")
    private String name;

    @NotNull(message = "Giá bán không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá bán phải lớn hơn 0")
    @Schema(description = "Giá bán hiện tại", example = "34990000")
    private BigDecimal price;

    @Schema(description = "Giá niêm yết ban đầu (nếu có giảm giá)", example = "36990000")
    private BigDecimal originalPrice;

    @NotNull(message = "Số lượng tồn kho không được để trống")
    @Min(value = 0, message = "Số lượng tồn kho không được âm")
    @Schema(description = "Số lượng trong kho", example = "50")
    private Integer stock;

    @Schema(description = "Trạng thái", example = "ACTIVE")
    @Builder.Default
    private String status = "ACTIVE";

    public static ProductVariantDto from(ProductVariant variant) {
        if (variant == null) return null;
        return ProductVariantDto.builder()
                .id(variant.getId())
                .sku(variant.getSku())
                .name(variant.getName())
                .price(variant.getPrice())
                .originalPrice(variant.getOriginalPrice())
                .stock(variant.getStock())
                .status(variant.getStatus())
                .build();
    }
}
