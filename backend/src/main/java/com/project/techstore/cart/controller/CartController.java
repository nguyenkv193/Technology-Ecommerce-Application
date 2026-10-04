package com.project.techstore.cart.controller;

import com.project.techstore.cart.dto.AddToCartRequest;
import com.project.techstore.cart.dto.CartResponse;
import com.project.techstore.cart.dto.UpdateCartItemRequest;
import com.project.techstore.cart.service.CartService;
import com.project.techstore.common.response.ApiResponse;
import com.project.techstore.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Shopping Cart", description = "Quản lý giỏ hàng người dùng")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Xem giỏ hàng của người dùng hiện tại")
    public ResponseEntity<ApiResponse<CartResponse>> getCart(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(cartService.getCart(currentUser.getId())));
    }

    @PostMapping("/items")
    @Operation(summary = "Thêm sản phẩm vào giỏ hàng")
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody AddToCartRequest request
    ) {
        CartResponse cart = cartService.addToCart(currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Thêm vào giỏ hàng thành công", cart));
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Cập nhật số lượng của một món trong giỏ")
    public ResponseEntity<ApiResponse<CartResponse>> updateItemQuantity(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request
    ) {
        CartResponse cart = cartService.updateQuantity(currentUser.getId(), itemId, request.getQuantity());
        return ResponseEntity.ok(ApiResponse.success("Cập nhật số lượng thành công", cart));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Xóa một món hàng khỏi giỏ")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long itemId
    ) {
        CartResponse cart = cartService.removeItem(currentUser.getId(), itemId);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa sản phẩm khỏi giỏ hàng", cart));
    }

    @DeleteMapping
    @Operation(summary = "Làm trống toàn bộ giỏ hàng")
    public ResponseEntity<ApiResponse<CartResponse>> clearCart(@AuthenticationPrincipal User currentUser) {
        CartResponse cart = cartService.clearCart(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Đã làm trống giỏ hàng", cart));
    }
}
