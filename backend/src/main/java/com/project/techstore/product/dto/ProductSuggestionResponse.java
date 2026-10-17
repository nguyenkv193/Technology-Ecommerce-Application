package com.project.techstore.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSuggestionResponse {

    private Long id;
    private String name;
    private String slug;
    private String brandName;
    private String categoryName;
    private String thumbnailUrl;
    private BigDecimal minPrice;

    public static ProductSuggestionResponse from(ProductResponse product) {
        return ProductSuggestionResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .brandName(product.getBrandName())
                .categoryName(product.getCategoryName())
                .thumbnailUrl(product.getThumbnailUrl())
                .minPrice(product.getMinPrice())
                .build();
    }
}
