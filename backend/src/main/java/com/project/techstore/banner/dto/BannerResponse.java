package com.project.techstore.banner.dto;

import com.project.techstore.banner.entity.Banner;
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
@Schema(description = "Thông tin chi tiết Banner")
public class BannerResponse {

    @Schema(description = "ID banner", example = "1")
    private Long id;

    @Schema(description = "Tiêu đề banner", example = "MacBook Pro M3 Pro")
    private String title;

    @Schema(description = "URL ảnh Desktop")
    private String imageUrl;

    @Schema(description = "URL ảnh Mobile")
    private String mobileImageUrl;

    @Schema(description = "Đường dẫn click điều hướng", example = "/products/3")
    private String linkUrl;

    @Schema(description = "Kiểu mở tab", example = "_self")
    private String targetType;

    @Schema(description = "Vị trí hiển thị", example = "HERO_SLIDER")
    private String position;

    @Schema(description = "Thứ tự sắp xếp", example = "1")
    private Integer displayOrder;

    @Schema(description = "Trạng thái", example = "ACTIVE")
    private String status;

    @Schema(description = "Thời điểm bắt đầu")
    private Instant startDate;

    @Schema(description = "Thời điểm kết thúc")
    private Instant endDate;

    @Schema(description = "Lượt click")
    private Long clickCount;

    @Schema(description = "Thời gian tạo")
    private Instant createdAt;

    public static BannerResponse from(Banner banner) {
        if (banner == null) return null;
        return BannerResponse.builder()
                .id(banner.getId())
                .title(banner.getTitle())
                .imageUrl(banner.getImageUrl())
                .mobileImageUrl(banner.getMobileImageUrl())
                .linkUrl(banner.getLinkUrl())
                .targetType(banner.getTargetType())
                .position(banner.getPosition())
                .displayOrder(banner.getDisplayOrder())
                .status(banner.getStatus())
                .startDate(banner.getStartDate())
                .endDate(banner.getEndDate())
                .clickCount(banner.getClickCount())
                .createdAt(banner.getCreatedAt())
                .build();
    }
}
