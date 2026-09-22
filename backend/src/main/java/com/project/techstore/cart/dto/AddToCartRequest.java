package com.project.techstore.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Yêu cầu thêm sản phẩm vào giỏ hàng")
public class AddToCartRequest {

    @NotNull(message = "ID biến thể SKU không được để trống")
    @Schema(description = "ID biến thể cấu hình sản phẩm", example = "1")
    private Long variantId;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng mua tối thiểu là 1")
    @Schema(description = "Số lượng muốn thêm", example = "1")
    private Integer quantity;
}
