package com.project.techstore.product.dto;

import com.project.techstore.product.entity.Product;
import com.project.techstore.product.entity.ProductImage;
import com.project.techstore.product.entity.ProductVariant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Thông tin tóm tắt sản phẩm trong danh sách catalog")
public class ProductResponse {

    @Schema(description = "ID sản phẩm", example = "1")
    private Long id;

    @Schema(description = "Tên sản phẩm", example = "iPhone 16 Pro Max")
    private String name;

    @Schema(description = "Slug URL", example = "iphone-16-pro-max")
    private String slug;

    @Schema(description = "ID danh mục")
    private Long categoryId;

    @Schema(description = "Tên danh mục", example = "Điện thoại")
    private String categoryName;

    @Schema(description = "Slug danh mục")
    private String categorySlug;

    @Schema(description = "ID thương hiệu")
    private Long brandId;

    @Schema(description = "Tên thương hiệu", example = "Apple")
    private String brandName;

    @Schema(description = "URL ảnh đại diện thumbnail")
    private String thumbnailUrl;

    @Schema(description = "Giá thấp nhất trong các biến thể", example = "34990000")
    private BigDecimal minPrice;

    @Schema(description = "Giá cao nhất trong các biến thể", example = "46990000")
    private BigDecimal maxPrice;

    @Schema(description = "Tổng tồn kho các biến thể", example = "100")
    private Integer totalStock;

    @Schema(description = "Trạng thái", example = "ACTIVE")
    private String status;

    @Schema(description = "Danh sách các biến thể cấu hình")
    private List<ProductVariantDto> variants;

    @Schema(description = "Thời gian tạo")
    private Instant createdAt;

    public static ProductResponse from(Product product) {
        if (product == null) return null;

        String thumb = null;
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            thumb = product.getImages().stream()
                    .filter(ProductImage::getIsThumbnail)
                    .map(ProductImage::getUrl)
                    .findFirst()
                    .orElse(product.getImages().get(0).getUrl());
        }

        BigDecimal min = null;
        BigDecimal max = null;
        int stockSum = 0;
        List<ProductVariantDto> variantDtos = Collections.emptyList();

        if (product.getVariants() != null && !product.getVariants().isEmpty()) {
            min = product.getVariants().stream()
                    .map(ProductVariant::getPrice)
                    .filter(Objects::nonNull)
                    .min(Comparator.naturalOrder())
                    .orElse(null);

            max = product.getVariants().stream()
                    .map(ProductVariant::getPrice)
                    .filter(Objects::nonNull)
                    .max(Comparator.naturalOrder())
                    .orElse(null);

            stockSum = product.getVariants().stream()
                    .map(ProductVariant::getStock)
                    .filter(Objects::nonNull)
                    .mapToInt(Integer::intValue)
                    .sum();

            variantDtos = product.getVariants().stream()
                    .map(ProductVariantDto::from)
                    .toList();
        }

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .categorySlug(product.getCategory() != null ? product.getCategory().getSlug() : null)
                .brandId(product.getBrand() != null ? product.getBrand().getId() : null)
                .brandName(product.getBrand() != null ? product.getBrand().getName() : null)
                .thumbnailUrl(thumb)
                .minPrice(min)
                .maxPrice(max)
                .totalStock(stockSum)
                .status(product.getStatus() != null ? product.getStatus().name() : null)
                .variants(variantDtos)
                .createdAt(product.getCreatedAt())
                .build();
    }
}
