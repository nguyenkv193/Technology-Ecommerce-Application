package com.project.techstore.behavior.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Từ khóa tìm kiếm thịnh hành")
public class TrendingKeywordResponse {

    @Schema(description = "Tên từ khóa tìm kiếm", example = "iPhone 16 Pro Max")
    private String keyword;

    @Schema(description = "Đường dẫn tìm kiếm trên storefront", example = "/products?keyword=iPhone+16+Pro+Max")
    private String link;

    @Schema(description = "Số lượt tìm kiếm ghi nhận được", example = "15")
    private Long searchCount;
}
