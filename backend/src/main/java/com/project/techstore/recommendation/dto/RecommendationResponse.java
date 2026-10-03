package com.project.techstore.recommendation.dto;

import com.project.techstore.product.dto.ProductResponse;
import java.util.List;

public record RecommendationResponse(String source, String fallbackReason, List<Item> recommendations) {
    public record Item(ProductResponse product, String source, Double score, String reason) {}
}
