package com.project.techstore.wishlist.controller;

import com.project.techstore.common.response.ApiResponse;
import com.project.techstore.user.entity.User;
import com.project.techstore.wishlist.dto.WishlistResponse;
import com.project.techstore.wishlist.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Wishlist", description = "Quản lý danh sách sản phẩm yêu thích (dữ liệu trọng số AI)")
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    @Operation(summary = "Lấy danh sách sản phẩm yêu thích của người dùng hiện tại")
    public ResponseEntity<ApiResponse<List<WishlistResponse>>> getWishlist(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(wishlistService.getUserWishlist(currentUser.getId())));
    }

    @PostMapping("/{productId}")
    @Operation(summary = "Thêm sản phẩm vào danh sách yêu thích")
    public ResponseEntity<ApiResponse<WishlistResponse>> addToWishlist(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long productId
    ) {
        WishlistResponse response = wishlistService.addToWishlist(currentUser.getId(), productId);
        return ResponseEntity.ok(ApiResponse.success("Đã thêm vào danh sách yêu thích", response));
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Xóa sản phẩm khỏi danh sách yêu thích")
    public ResponseEntity<ApiResponse<Void>> removeFromWishlist(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long productId
    ) {
        wishlistService.removeFromWishlist(currentUser.getId(), productId);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa khỏi danh sách yêu thích", null));
    }

    @GetMapping("/check/{productId}")
    @Operation(summary = "Kiểm tra xem sản phẩm đã có trong danh sách yêu thích chưa")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkWishlist(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long productId
    ) {
        boolean isFav = wishlistService.isWishlisted(currentUser.getId(), productId);
        return ResponseEntity.ok(ApiResponse.success(Map.of("isWishlisted", isFav)));
    }
}
