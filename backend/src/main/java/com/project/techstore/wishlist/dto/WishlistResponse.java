package com.project.techstore.wishlist.dto;

import com.project.techstore.product.entity.Product;
import com.project.techstore.product.entity.ProductImage;
import com.project.techstore.product.entity.ProductVariant;
import com.project.techstore.wishlist.entity.Wishlist;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Sản phẩm trong danh sách yêu thích")
public class WishlistResponse {

    @Schema(description = "ID mục wishlist", example = "1")
    private Long id;

    @Schema(description = "ID sản phẩm", example = "10")
    private Long productId;

    @Schema(description = "Tên sản phẩm", example = "MacBook Pro M3")
    private String productName;

    @Schema(description = "Slug sản phẩm", example = "macbook-pro-m3")
    private String productSlug;

    @Schema(description = "Tên thương hiệu", example = "Apple")
    private String brandName;

    @Schema(description = "Tên danh mục", example = "Laptop")
    private String categoryName;

    @Schema(description = "Hình ảnh đại diện")
    private String thumbnailUrl;

    @Schema(description = "Giá thấp nhất", example = "39990000")
    private BigDecimal minPrice;

    @Schema(description = "Thời gian thêm vào wishlist")
    private Instant addedAt;

    public static WishlistResponse from(Wishlist wishlist) {
        if (wishlist == null) return null;

        Product product = wishlist.getProduct();
        String thumb = null;
        BigDecimal min = null;

        if (product != null) {
            if (product.getImages() != null && !product.getImages().isEmpty()) {
                thumb = product.getImages().stream()
                        .filter(ProductImage::getIsThumbnail)
                        .map(ProductImage::getUrl)
                        .findFirst()
                        .orElse(product.getImages().get(0).getUrl());
            }

            if (product.getVariants() != null && !product.getVariants().isEmpty()) {
                min = product.getVariants().stream()
                        .map(ProductVariant::getPrice)
                        .filter(Objects::nonNull)
                        .min(Comparator.naturalOrder())
                        .orElse(null);
            }
        }

        return WishlistResponse.builder()
                .id(wishlist.getId())
                .productId(product != null ? product.getId() : null)
                .productName(product != null ? product.getName() : null)
                .productSlug(product != null ? product.getSlug() : null)
                .brandName(product != null && product.getBrand() != null ? product.getBrand().getName() : null)
                .categoryName(product != null && product.getCategory() != null ? product.getCategory().getName() : null)
                .thumbnailUrl(thumb)
                .minPrice(min)
                .addedAt(wishlist.getCreatedAt())
                .build();
    }
}
