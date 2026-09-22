package com.project.techstore.product.dto;

import com.project.techstore.product.entity.ProductImage;
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
@Schema(description = "Hình ảnh sản phẩm")
public class ProductImageDto {

    private Long id;

    @NotBlank(message = "Đường dẫn hình ảnh không được để trống")
    @Schema(description = "URL hình ảnh", example = "https://images.unsplash.com/photo-1695048133142-1a20484d2569")
    private String url;

    @Schema(description = "Thứ tự sắp xếp hiển thị", example = "1")
    @Builder.Default
    private Integer sortOrder = 0;

    @Schema(description = "Có phải ảnh đại diện thumbnail không", example = "true")
    @Builder.Default
    private Boolean isThumbnail = false;

    public static ProductImageDto from(ProductImage image) {
        if (image == null) return null;
        return ProductImageDto.builder()
                .id(image.getId())
                .url(image.getUrl())
                .sortOrder(image.getSortOrder())
                .isThumbnail(image.getIsThumbnail())
                .build();
    }
}
