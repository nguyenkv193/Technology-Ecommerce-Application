package com.project.techstore.cart.dto;

import com.project.techstore.cart.entity.CartItem;
import com.project.techstore.product.entity.Product;
import com.project.techstore.product.entity.ProductImage;
import com.project.techstore.product.entity.ProductVariant;
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
@Schema(description = "Thông tin chi tiết một món hàng trong giỏ")
public class CartItemResponse {

    @Schema(description = "ID mục giỏ hàng", example = "1")
    private Long id;

    @Schema(description = "ID biến thể SKU", example = "10")
    private Long variantId;

    @Schema(description = "ID sản phẩm", example = "5")
    private Long productId;

    @Schema(description = "Tên sản phẩm", example = "iPhone 16 Pro Max")
    private String productName;

    @Schema(description = "Slug sản phẩm", example = "iphone-16-pro-max")
    private String productSlug;

    @Schema(description = "Tên phiên bản cấu hình", example = "256GB - Titan Tự Nhiên")
    private String variantName;

    @Schema(description = "Mã SKU", example = "IP16PM-256-NAT")
    private String sku;

    @Schema(description = "Hình ảnh đại diện")
    private String thumbnailUrl;

    @Schema(description = "Đơn giá", example = "34990000")
    private BigDecimal price;

    @Schema(description = "Tồn kho hiện có", example = "50")
    private Integer stock;

    @Schema(description = "Số lượng đang chọn trong giỏ", example = "1")
    private Integer quantity;

    @Schema(description = "Tổng tiền cho mục này", example = "34990000")
    private BigDecimal subTotal;

    public static CartItemResponse from(CartItem item) {
        if (item == null) return null;

        ProductVariant variant = item.getVariant();
        Product product = variant != null ? variant.getProduct() : null;

        String thumb = null;
        if (product != null && product.getImages() != null && !product.getImages().isEmpty()) {
            thumb = product.getImages().stream()
                    .filter(ProductImage::getIsThumbnail)
                    .map(ProductImage::getUrl)
                    .findFirst()
                    .orElse(product.getImages().get(0).getUrl());
        }

        BigDecimal price = variant != null ? variant.getPrice() : BigDecimal.ZERO;
        BigDecimal subTotal = price.multiply(BigDecimal.valueOf(item.getQuantity()));

        return CartItemResponse.builder()
                .id(item.getId())
                .variantId(variant != null ? variant.getId() : null)
                .productId(product != null ? product.getId() : null)
                .productName(product != null ? product.getName() : null)
                .productSlug(product != null ? product.getSlug() : null)
                .variantName(variant != null ? variant.getName() : null)
                .sku(variant != null ? variant.getSku() : null)
                .thumbnailUrl(thumb)
                .price(price)
                .stock(variant != null ? variant.getStock() : 0)
                .quantity(item.getQuantity())
                .subTotal(subTotal)
                .build();
    }
}
