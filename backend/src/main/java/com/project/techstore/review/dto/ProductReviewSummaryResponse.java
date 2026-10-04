package com.project.techstore.review.dto;

import com.project.techstore.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Tổng hợp đánh giá sản phẩm (Điểm trung bình và danh sách phân trang)")
public class ProductReviewSummaryResponse {

    @Schema(description = "ID sản phẩm", example = "1")
    private Long productId;

    @Schema(description = "Điểm đánh giá trung bình", example = "4.8")
    private Double averageRating;

    @Schema(description = "Tổng số lượt đánh giá", example = "125")
    private Long totalReviews;

    @Schema(description = "Danh sách đánh giá phân trang")
    private PageResponse<ReviewResponse> reviews;
}
