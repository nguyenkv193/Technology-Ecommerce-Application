package com.project.techstore.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
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
@Schema(description = "Yêu cầu đánh giá sản phẩm")
public class CreateReviewRequest {

    @NotNull(message = "ID sản phẩm không được để trống")
    @Schema(description = "ID sản phẩm", example = "1")
    private Long productId;

    @NotNull(message = "Số sao đánh giá không được để trống")
    @Min(value = 1, message = "Đánh giá tối thiểu là 1 sao")
    @Max(value = 5, message = "Đánh giá tối đa là 5 sao")
    @Schema(description = "Số sao đánh giá (1 đến 5)", example = "5")
    private Integer rating;

    @Schema(description = "Nhận xét chi tiết", example = "Máy dùng rất mượt, pin trâu, màn hình đẹp xuất sắc!")
    private String comment;
}
