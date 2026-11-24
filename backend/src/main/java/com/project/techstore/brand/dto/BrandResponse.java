package com.project.techstore.brand.dto;

import com.project.techstore.brand.entity.Brand;
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
@Schema(description = "Thông tin chi tiết Thương hiệu")
public class BrandResponse {

    @Schema(description = "ID thương hiệu", example = "1")
    private Long id;

    @Schema(description = "Tên thương hiệu", example = "Apple")
    private String name;

    @Schema(description = "Slug URL", example = "apple")
    private String slug;

    @Schema(description = "Mô tả", example = "Tập đoàn công nghệ")
    private String description;

    @Schema(description = "URL logo")
    private String logoUrl;

    @Schema(description = "Website chính thức")
    private String websiteUrl;

    @Schema(description = "Quốc gia xuất xứ", example = "Hoa Kỳ")
    private String originCountry;

    @Schema(description = "Số lượng sản phẩm thuộc thương hiệu", example = "8")
    private Long productCount;

    @Schema(description = "Trạng thái", example = "ACTIVE")
    private String status;

    @Schema(description = "Thời gian tạo")
    private Instant createdAt;

    public static BrandResponse from(Brand brand) {
        return from(brand, 0L);
    }

    public static BrandResponse from(Brand brand, Long productCount) {
        if (brand == null) return null;
        return BrandResponse.builder()
                .id(brand.getId())
                .name(brand.getName())
                .slug(brand.getSlug())
                .description(brand.getDescription())
                .logoUrl(brand.getLogoUrl())
                .websiteUrl(brand.getWebsiteUrl())
                .originCountry(brand.getOriginCountry())
                .productCount(productCount != null ? productCount : 0L)
                .status(brand.getStatus())
                .createdAt(brand.getCreatedAt())
                .build();
    }
}
