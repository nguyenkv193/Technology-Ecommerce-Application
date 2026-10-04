package com.project.techstore.order.controller;

import com.project.techstore.common.dto.PageResponse;
import com.project.techstore.common.response.ApiResponse;
import com.project.techstore.order.dto.CreateOrderFromCartRequest;
import com.project.techstore.order.dto.OrderResponse;
import com.project.techstore.order.dto.UpdateOrderStatusRequest;
import com.project.techstore.order.service.OrderService;
import com.project.techstore.user.entity.Role;
import com.project.techstore.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Order & Checkout", description = "Quản lý đặt hàng, thanh toán và xử lý đơn hàng")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    @Operation(summary = "Đặt hàng từ giỏ hàng hiện tại (khấu trừ tồn kho & snapshot giá)")
    public ResponseEntity<ApiResponse<OrderResponse>> checkout(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody CreateOrderFromCartRequest request
    ) {
        OrderResponse order = orderService.createOrderFromCart(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Đặt hàng thành công", order));
    }

    @GetMapping
    @Operation(summary = "Xem lịch sử đơn hàng của người dùng hiện tại (phân trang)")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getMyOrders(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<OrderResponse> orders = orderService.getUserOrders(currentUser.getId(), page, size);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Xem chi tiết một đơn hàng")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderDetails(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id
    ) {
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        OrderResponse order = orderService.getOrderDetails(currentUser.getId(), id, isAdmin);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Hủy đơn hàng (nếu đang ở trạng thái PENDING hoặc CONFIRMED, tự động hoàn kho)")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id
    ) {
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        OrderResponse order = orderService.cancelOrder(currentUser.getId(), id, isAdmin);
        return ResponseEntity.ok(ApiResponse.success("Đã hủy đơn hàng thành công", order));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật trạng thái đơn hàng (Admin)")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request
    ) {
        OrderResponse updated = orderService.updateOrderStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái đơn hàng thành công", updated));
    }
}
