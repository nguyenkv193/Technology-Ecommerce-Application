package com.project.techstore.order.dto;

import com.project.techstore.order.entity.Order;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Thông tin chi tiết đơn hàng")
public class OrderResponse {

    @Schema(description = "ID đơn hàng", example = "1")
    private Long id;

    @Schema(description = "Mã đơn hàng duy nhất", example = "ORD-20260917-ABC123")
    private String orderCode;

    @Schema(description = "ID người đặt hàng", example = "100")
    private Long userId;

    @Schema(description = "Họ tên người nhận", example = "Nguyễn Văn A")
    private String recipientName;

    @Schema(description = "Số điện thoại nhận hàng", example = "0912345678")
    private String phoneNumber;

    @Schema(description = "Địa chỉ nhận hàng", example = "Tầng 5, Keangnam, Hà Nội")
    private String shippingAddress;

    @Schema(description = "Ghi chú đơn hàng")
    private String note;

    @Schema(description = "Tổng tiền hàng", example = "39990000")
    private BigDecimal totalAmount;

    @Schema(description = "Phí giao hàng", example = "0")
    private BigDecimal shippingFee;

    @Schema(description = "Giảm giá", example = "0")
    private BigDecimal discountAmount;

    @Schema(description = "Tổng tiền thanh toán cuối cùng", example = "39990000")
    private BigDecimal finalAmount;

    @Schema(description = "Trạng thái đơn hàng (PENDING, CONFIRMED, SHIPPING, DELIVERED, CANCELLED)", example = "PENDING")
    private String orderStatus;

    @Schema(description = "Phương thức thanh toán", example = "COD")
    private String paymentMethod;

    @Schema(description = "Trạng thái thanh toán (PENDING, PAID, FAILED, REFUNDED)", example = "PENDING")
    private String paymentStatus;

    @Schema(description = "Danh sách sản phẩm trong đơn")
    private List<OrderItemResponse> items;

    @Schema(description = "Thời gian tạo đơn")
    private Instant createdAt;

    @Schema(description = "Thời gian cập nhật đơn")
    private Instant updatedAt;

    public static OrderResponse from(Order order) {
        if (order == null) return null;

        List<OrderItemResponse> itemDtos = order.getItems() != null
                ? order.getItems().stream().map(OrderItemResponse::from).toList()
                : Collections.emptyList();

        return OrderResponse.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .userId(order.getUser() != null ? order.getUser().getId() : null)
                .recipientName(order.getRecipientName())
                .phoneNumber(order.getPhoneNumber())
                .shippingAddress(order.getShippingAddress())
                .note(order.getNote())
                .totalAmount(order.getTotalAmount())
                .shippingFee(order.getShippingFee())
                .discountAmount(order.getDiscountAmount())
                .finalAmount(order.getFinalAmount())
                .orderStatus(order.getOrderStatus() != null ? order.getOrderStatus().name() : null)
                .paymentMethod(order.getPaymentMethod() != null ? order.getPaymentMethod().name() : null)
                .paymentStatus(order.getPaymentStatus() != null ? order.getPaymentStatus().name() : null)
                .items(itemDtos)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
