package com.project.techstore.behavior.dto;

import com.project.techstore.behavior.entity.UserBehavior;
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
@Schema(description = "Thông tin sự kiện hành vi người dùng đã ghi nhận")
public class UserBehaviorResponse {

    @Schema(description = "ID sự kiện", example = "1")
    private Long id;

    @Schema(description = "ID người dùng", example = "100")
    private Long userId;

    @Schema(description = "Session ID", example = "sess_abc123")
    private String sessionId;

    @Schema(description = "ID sản phẩm", example = "5")
    private Long productId;

    @Schema(description = "Tên sản phẩm", example = "iPhone 16 Pro Max")
    private String productName;

    @Schema(description = "Loại hành vi", example = "VIEW")
    private String actionType;

    @Schema(description = "Giá trị bổ sung")
    private String actionValue;

    @Schema(description = "Thời gian ghi nhận")
    private Instant createdAt;

    public static UserBehaviorResponse from(UserBehavior b) {
        if (b == null) return null;
        return UserBehaviorResponse.builder()
                .id(b.getId())
                .userId(b.getUser() != null ? b.getUser().getId() : null)
                .sessionId(b.getSessionId())
                .productId(b.getProduct() != null ? b.getProduct().getId() : null)
                .productName(b.getProduct() != null ? b.getProduct().getName() : null)
                .actionType(b.getActionType() != null ? b.getActionType().name() : null)
                .actionValue(b.getActionValue())
                .createdAt(b.getCreatedAt())
                .build();
    }
}
