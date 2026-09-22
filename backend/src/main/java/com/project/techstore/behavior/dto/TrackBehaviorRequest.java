package com.project.techstore.behavior.dto;

import com.project.techstore.behavior.entity.BehaviorType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Yêu cầu ghi nhận sự kiện hành vi người dùng (Clickstream Tracking)")
public class TrackBehaviorRequest {

    @Schema(description = "ID sản phẩm liên quan (nếu có)", example = "1")
    private Long productId;

    @NotNull(message = "Loại hành vi không được để trống (VIEW, SEARCH, ADD_TO_CART, WISHLIST, PURCHASE, RATING)")
    @Schema(description = "Loại hành vi", example = "VIEW")
    private BehaviorType actionType;

    @Schema(description = "Giá trị bổ sung (Ví dụ: từ khóa tìm kiếm, số sao, thời gian dừng xem)", example = "macbook pro m3")
    private String actionValue;

    @Schema(description = "Session ID của trình duyệt (dành cho cả khách vãng lai chưa đăng nhập)", example = "sess_abc123456")
    private String sessionId;
}
