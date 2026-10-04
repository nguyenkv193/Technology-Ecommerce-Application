package com.project.techstore.cart.dto;

import com.project.techstore.cart.entity.Cart;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Thông tin toàn bộ giỏ hàng người dùng")
public class CartResponse {

    @Schema(description = "ID giỏ hàng", example = "1")
    private Long id;

    @Schema(description = "ID người dùng", example = "100")
    private Long userId;

    @Schema(description = "Danh sách sản phẩm trong giỏ")
    private List<CartItemResponse> items;

    @Schema(description = "Tổng số lượng sản phẩm", example = "3")
    private Integer totalItems;

    @Schema(description = "Tổng tiền giỏ hàng", example = "75980000")
    private BigDecimal totalPrice;

    public static CartResponse from(Cart cart) {
        if (cart == null) return null;

        List<CartItemResponse> itemDtos = cart.getItems() != null
                ? cart.getItems().stream().map(CartItemResponse::from).toList()
                : Collections.emptyList();

        int totalCount = itemDtos.stream().mapToInt(CartItemResponse::getQuantity).sum();
        BigDecimal totalSum = itemDtos.stream()
                .map(CartItemResponse::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUser() != null ? cart.getUser().getId() : null)
                .items(itemDtos)
                .totalItems(totalCount)
                .totalPrice(totalSum)
                .build();
    }
}
