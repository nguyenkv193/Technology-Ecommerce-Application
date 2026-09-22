package com.project.techstore.behavior.dto;

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
@Schema(description = "Dữ liệu ma trận tương tác User-Item xuất cho dịch vụ AI Recommendation huấn luyện mô hình")
public class UserInteractionExportDto {

    @Schema(description = "ID người dùng", example = "100")
    private Long userId;

    @Schema(description = "ID sản phẩm", example = "5")
    private Long productId;

    @Schema(description = "Điểm tương tác tổng hợp (Implicit Preference Score)", example = "8.5")
    private Double score;

    @Schema(description = "Tổng số lần tương tác", example = "4")
    private Long interactionCount;

    @Schema(description = "Thời gian tương tác gần nhất")
    private Instant lastInteractedAt;
}
