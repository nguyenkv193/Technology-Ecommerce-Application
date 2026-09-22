package com.project.techstore.category.dto;

import com.project.techstore.category.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Thông tin chi tiết Danh mục sản phẩm")
public class CategoryResponse {

    @Schema(description = "ID danh mục", example = "1")
    private Long id;

    @Schema(description = "Tên danh mục", example = "Laptop")
    private String name;

    @Schema(description = "Slug URL", example = "laptop")
    private String slug;

    @Schema(description = "Mô tả", example = "Các dòng máy tính xách tay")
    private String description;

    @Schema(description = "ID danh mục cha", example = "null")
    private Long parentId;

    @Schema(description = "Tên danh mục cha")
    private String parentName;

    @Schema(description = "URL hình ảnh đại diện")
    private String imageUrl;

    @Schema(description = "Danh sách các danh mục con (nếu có)")
    private List<CategoryResponse> subCategories;

    @Schema(description = "Trạng thái", example = "ACTIVE")
    private String status;

    @Schema(description = "Thời gian tạo")
    private Instant createdAt;

    public static CategoryResponse from(Category category, boolean includeSubCategories) {
        if (category == null) return null;

        List<CategoryResponse> subs = null;
        if (includeSubCategories && category.getSubCategories() != null) {
            subs = category.getSubCategories().stream()
                    .map(sub -> CategoryResponse.from(sub, false))
                    .toList();
        }

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .parentName(category.getParent() != null ? category.getParent().getName() : null)
                .imageUrl(category.getImageUrl())
                .subCategories(subs)
                .status(category.getStatus())
                .createdAt(category.getCreatedAt())
                .build();
    }
}
