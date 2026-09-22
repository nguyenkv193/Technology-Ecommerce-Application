package com.project.techstore.brand.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Yêu cầu tạo mới hoặc cập nhật Thương hiệu")
public class BrandRequest {

    @NotBlank(message = "Tên thương hiệu không được để trống")
    @Schema(description = "Tên thương hiệu", example = "Apple")
    private String name;

    @Schema(description = "Slug URL thương hiệu", example = "apple")
    private String slug;

    @Schema(description = "Mô tả thương hiệu", example = "Tập đoàn công nghệ đa quốc gia của Mỹ")
    private String description;

    @Schema(description = "URL logo thương hiệu", example = "https://res.cloudinary.com/.../apple.png")
    private String logoUrl;

    @Schema(description = "Website chính thức", example = "https://apple.com")
    private String websiteUrl;
}
