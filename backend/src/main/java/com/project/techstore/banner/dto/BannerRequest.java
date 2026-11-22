package com.project.techstore.banner.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dữ liệu tạo mới hoặc cập nhật Banner")
public class BannerRequest {

    @NotBlank(message = "Tiêu đề banner không được để trống")
    @Size(max = 255, message = "Tiêu đề không được vượt quá 255 ký tự")
    @Schema(description = "Tiêu đề banner", example = "MacBook Pro M3 Pro")
    private String title;

    @NotBlank(message = "URL ảnh không được để trống")
    @Size(max = 500, message = "URL ảnh không được vượt quá 500 ký tự")
    @Schema(description = "URL ảnh Desktop", example = "https://images.unsplash.com/...")
    private String imageUrl;

    @Size(max = 500, message = "URL ảnh Mobile không được vượt quá 500 ký tự")
    @Schema(description = "URL ảnh Mobile")
    private String mobileImageUrl;

    @NotBlank(message = "Đường dẫn liên kết không được để trống")
    @Size(max = 500, message = "Đường dẫn liên kết không được vượt quá 500 ký tự")
    @Schema(description = "Đường dẫn liên kết điều hướng", example = "/products/3")
    private String linkUrl;

    @Builder.Default
    @Schema(description = "Kiểu mở liên kết (_self, _blank)", example = "_self")
    private String targetType = "_self";

    @Builder.Default
    @Schema(description = "Vị trí hiển thị (HERO_SLIDER, SIDE_PROMO, BOTTOM_CARD, POPUP)", example = "HERO_SLIDER")
    private String position = "HERO_SLIDER";

    @Builder.Default
    @Schema(description = "Thứ tự hiển thị", example = "1")
    private Integer displayOrder = 0;

    @Builder.Default
    @Schema(description = "Trạng thái (ACTIVE, INACTIVE)", example = "ACTIVE")
    private String status = "ACTIVE";

    @Schema(description = "Thời gian bắt đầu hiển thị")
    private Instant startDate;

    @Schema(description = "Thời gian kết thúc hiển thị")
    private Instant endDate;
}
