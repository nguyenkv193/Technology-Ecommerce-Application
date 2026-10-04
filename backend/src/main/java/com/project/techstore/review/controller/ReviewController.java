package com.project.techstore.review.controller;

import com.project.techstore.common.dto.PageResponse;
import com.project.techstore.common.response.ApiResponse;
import com.project.techstore.review.dto.CreateReviewRequest;
import com.project.techstore.review.dto.ProductReviewSummaryResponse;
import com.project.techstore.review.dto.ReviewResponse;
import com.project.techstore.review.service.ReviewService;
import com.project.techstore.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Tag(name = "Product Reviews", description = "Đánh giá và nhận xét sản phẩm")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/product/{productId}")
    @Operation(summary = "Lấy danh sách đánh giá của sản phẩm (phân trang)")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<ReviewResponse> response = reviewService.getProductReviews(productId, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/product/{productId}/summary")
    @Operation(summary = "Lấy thống kê điểm đánh giá trung bình & danh sách đánh giá sản phẩm")
    public ResponseEntity<ApiResponse<ProductReviewSummaryResponse>> getProductReviewSummary(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        ProductReviewSummaryResponse summary = reviewService.getProductReviewSummary(productId, page, size);
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    @PostMapping
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Gửi đánh giá sản phẩm (1-5 sao kèm nhận xét)")
    public ResponseEntity<ApiResponse<ReviewResponse>> submitReview(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody CreateReviewRequest request
    ) {
        ReviewResponse response = reviewService.createOrUpdateReview(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Đánh giá sản phẩm thành công", response));
    }
}
