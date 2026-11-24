package com.project.techstore.behavior.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BehaviorAnalyticsResponse {

    private int days;
    private InteractionSummary summary;
    private List<DailyInteractionPoint> dailySeries;
    private List<TopInteractedProduct> topProducts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InteractionSummary {
        private long totalViews;
        private long totalWishlists;
        private long totalAddToCart;
        private long totalPurchases;
        private long totalSearches;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyInteractionPoint {
        private String date;
        private long views;
        private long wishlists;
        private long addToCarts;
        private long purchases;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopInteractedProduct {
        private Long productId;
        private String productName;
        private String slug;
        private String categoryName;
        private String thumbnailUrl;
        private BigDecimal minPrice;
        private long viewCount;
        private long wishlistCount;
        private long addToCartCount;
        private long purchaseCount;
        private double interactionScore;
    }
}
