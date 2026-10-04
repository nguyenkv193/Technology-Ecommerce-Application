package com.project.techstore.order.dto;

import com.project.techstore.order.entity.OrderStatus;
import com.project.techstore.order.entity.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Yêu cầu cập nhật trạng thái đơn hàng (Admin)")
public class UpdateOrderStatusRequest {

    @NotNull(message = "Trạng thái đơn hàng không được để trống")
    @Schema(description = "Trạng thái đơn hàng (PENDING, CONFIRMED, SHIPPING, DELIVERED, CANCELLED)", example = "CONFIRMED")
    private OrderStatus orderStatus;

    @Schema(description = "Trạng thái thanh toán (PENDING, PAID, FAILED, REFUNDED)", example = "PAID")
    private PaymentStatus paymentStatus;
}
