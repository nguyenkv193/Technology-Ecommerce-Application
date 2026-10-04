package com.project.techstore.category.dto;

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
@Schema(description = "Yêu cầu tạo mới hoặc cập nhật Danh mục")
public class CategoryRequest {

    @NotBlank(message = "Tên danh mục không được để trống")
    @Schema(description = "Tên danh mục", example = "Laptop")
    private String name;

    @Schema(description = "Mã định danh slug URL (nếu trống sẽ tự sinh)", example = "laptop")
    private String slug;

    @Schema(description = "Mô tả danh mục", example = "Các dòng máy tính xách tay văn phòng và gaming")
    private String description;

    @Schema(description = "ID của danh mục cha (để trống nếu là danh mục gốc)", example = "1")
    private Long parentId;

    @Schema(description = "URL hình ảnh đại diện", example = "https://res.cloudinary.com/.../laptop.jpg")
    private String imageUrl;
}
