package com.project.techstore.product.dto;

import com.project.techstore.brand.dto.BrandResponse;
import com.project.techstore.category.dto.CategoryResponse;
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
@Schema(description = "Chi tiết đầy đủ thông tin sản phẩm công nghệ (kèm biến thể, thông số, ảnh)")
public class ProductDetailResponse {

    @Schema(description = "ID sản phẩm", example = "1")
    private Long id;

    @Schema(description = "Tên sản phẩm", example = "iPhone 16 Pro Max")
    private String name;

    @Schema(description = "Slug URL", example = "iphone-16-pro-max")
    private String slug;

    @Schema(description = "Mô tả chi tiết")
    private String description;

    @Schema(description = "Trạng thái", example = "ACTIVE")
    private String status;

    @Schema(description = "Thông tin danh mục")
    private CategoryResponse category;

    @Schema(description = "ID danh mục")
    private Long categoryId;

    @Schema(description = "Tên danh mục", example = "Điện thoại")
    private String categoryName;

    @Schema(description = "Slug danh mục")
    private String categorySlug;

    @Schema(description = "Thông tin thương hiệu")
    private BrandResponse brand;

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

    @Schema(description = "Danh sách thông số kỹ thuật (RAM, CPU, GPU, Màn hình,...)")
    private List<ProductAttributeDto> attributes;

    @Schema(description = "Danh sách các biến thể cấu hình / SKU")
    private List<ProductVariantDto> variants;

    @Schema(description = "Danh sách hình ảnh sản phẩm")
    private List<ProductImageDto> images;

    @Schema(description = "Thời gian tạo")
    private Instant createdAt;

    @Schema(description = "Thời gian cập nhật")
    private Instant updatedAt;

    public static ProductDetailResponse from(Product product) {
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
        }

        List<ProductAttributeDto> attrDtos = product.getAttributes() != null
                ? product.getAttributes().stream().map(ProductAttributeDto::from).toList()
                : Collections.emptyList();

        List<ProductVariantDto> variantDtos = product.getVariants() != null
                ? product.getVariants().stream().map(ProductVariantDto::from).toList()
                : Collections.emptyList();

        List<ProductImageDto> imageDtos = product.getImages() != null
                ? product.getImages().stream().map(ProductImageDto::from).toList()
                : Collections.emptyList();

        return ProductDetailResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .description(product.getDescription())
                .status(product.getStatus() != null ? product.getStatus().name() : null)
                .category(CategoryResponse.from(product.getCategory(), false))
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .categorySlug(product.getCategory() != null ? product.getCategory().getSlug() : null)
                .brand(BrandResponse.from(product.getBrand()))
                .brandId(product.getBrand() != null ? product.getBrand().getId() : null)
                .brandName(product.getBrand() != null ? product.getBrand().getName() : null)
                .thumbnailUrl(thumb)
                .minPrice(min)
                .maxPrice(max)
                .totalStock(stockSum)
                .attributes(attrDtos)
                .variants(variantDtos)
                .images(imageDtos)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
