package com.project.techstore.product.dto;

import com.project.techstore.product.entity.Product;
import java.util.List;

public record ProductRecommendationFeature(Long id, String name, String slug, Long categoryId,
                                          String category, String brand, String description,
                                          List<ProductAttributeDto> attributes) {
    public static ProductRecommendationFeature from(Product product) {
        return new ProductRecommendationFeature(product.getId(), product.getName(), product.getSlug(),
                product.getCategory().getId(), product.getCategory().getName(), product.getBrand().getName(),
                product.getDescription(), product.getAttributes().stream().map(ProductAttributeDto::from).toList());
    }
}
