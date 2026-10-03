package com.project.techstore.order.dto;

import com.project.techstore.order.entity.OrderItem;
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
@Schema(description = "Thông tin chi tiết một món hàng trong đơn đặt")
public class OrderItemResponse {

    @Schema(description = "ID món hàng", example = "1")
    private Long id;

    @Schema(description = "ID biến thể SKU", example = "10")
    private Long variantId;

    @Schema(description = "ID sản phẩm phục vụ ghi nhận lịch sử mua hàng")
    private Long productId;

    @Schema(description = "Tên sản phẩm", example = "MacBook Pro M3")
    private String productName;

    @Schema(description = "Tên phiên bản cấu hình", example = "16GB RAM / 512GB SSD")
    private String variantName;

    @Schema(description = "Mã SKU", example = "MBP-M3-16-512")
    private String sku;

    @Schema(description = "Hình ảnh đại diện sản phẩm")
    private String thumbnailUrl;

    @Schema(description = "Đơn giá mua", example = "39990000")
    private BigDecimal price;

    @Schema(description = "Số lượng mua", example = "1")
    private Integer quantity;

    @Schema(description = "Thành tiền", example = "39990000")
    private BigDecimal totalPrice;

    public static OrderItemResponse from(OrderItem item) {
        if (item == null) return null;
        return OrderItemResponse.builder()
                .id(item.getId())
                .variantId(item.getVariant() != null ? item.getVariant().getId() : null)
                .productId(item.getVariant() != null && item.getVariant().getProduct() != null
                        ? item.getVariant().getProduct().getId() : null)
                .productName(item.getProductName())
                .variantName(item.getVariantName())
                .sku(item.getSku())
                .thumbnailUrl(item.getThumbnailUrl())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .totalPrice(item.getTotalPrice())
                .build();
    }
}
