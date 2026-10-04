package com.project.techstore.order.dto;

import com.project.techstore.order.entity.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Yêu cầu đặt hàng từ giỏ hàng hiện tại (Checkout)")
public class CreateOrderFromCartRequest {

    @NotBlank(message = "Tên người nhận không được để trống")
    @Schema(description = "Họ tên người nhận hàng", example = "Nguyễn Văn A")
    private String recipientName;

    @NotBlank(message = "Số điện thoại nhận hàng không được để trống")
    @Schema(description = "Số điện thoại nhận hàng", example = "0912345678")
    private String phoneNumber;

    @NotBlank(message = "Địa chỉ giao hàng không được để trống")
    @Schema(description = "Địa chỉ chi tiết nhận hàng", example = "Tầng 5, Tòa Keangnam, Mễ Trì, Nam Từ Liêm, Hà Nội")
    private String shippingAddress;

    @Schema(description = "Ghi chú đơn hàng", example = "Giao giờ hành chính giúp tôi")
    private String note;

    @Schema(description = "Phương thức thanh toán (COD, VNPAY, MOMO, BANK_TRANSFER)", example = "COD")
    @Builder.Default
    private PaymentMethod paymentMethod = PaymentMethod.COD;
}
