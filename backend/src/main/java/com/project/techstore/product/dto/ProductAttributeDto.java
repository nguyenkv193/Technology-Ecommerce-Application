package com.project.techstore.product.dto;

import com.project.techstore.product.entity.ProductAttribute;
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
@Schema(description = "Thuộc tính thông số kỹ thuật sản phẩm (RAM, CPU, Màn hình, Dung lượng)")
public class ProductAttributeDto {

    private Long id;

    @NotBlank(message = "Tên thuộc tính không được để trống (ví dụ: RAM, CPU, GPU)")
    @Schema(description = "Tên thuộc tính", example = "RAM")
    private String name;

    @NotBlank(message = "Giá trị thuộc tính không được để trống (ví dụ: 16GB, Apple M3 Pro)")
    @Schema(description = "Giá trị thuộc tính", example = "16GB")
    private String value;

    public static ProductAttributeDto from(ProductAttribute attribute) {
        if (attribute == null) return null;
        return ProductAttributeDto.builder()
                .id(attribute.getId())
                .name(attribute.getName())
                .value(attribute.getValue())
                .build();
    }
}
