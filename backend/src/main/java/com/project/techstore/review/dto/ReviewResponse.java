package com.project.techstore.review.dto;

import com.project.techstore.review.entity.ProductReview;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Thông tin chi tiết đánh giá sản phẩm")
public class ReviewResponse {

    @Schema(description = "ID đánh giá", example = "1")
    private Long id;

    @Schema(description = "ID người dùng", example = "100")
    private Long userId;

    @Schema(description = "Tên hiển thị người đánh giá", example = "Nguyễn Văn A")
    private String userFullName;

    @Schema(description = "ID sản phẩm", example = "5")
    private Long productId;

    @Schema(description = "Số sao (1-5)", example = "5")
    private Integer rating;

    @Schema(description = "Nội dung nhận xét", example = "Sản phẩm tốt, đóng gói cẩn thận")
    private String comment;

    @Schema(description = "Thời gian gửi đánh giá")
    private Instant createdAt;

    public static ReviewResponse from(ProductReview review) {
        if (review == null) return null;
        return ReviewResponse.builder()
                .id(review.getId())
                .userId(review.getUser() != null ? review.getUser().getId() : null)
                .userFullName(review.getUser() != null ? review.getUser().getFullName() : "Ẩn danh")
                .productId(review.getProduct() != null ? review.getProduct().getId() : null)
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
