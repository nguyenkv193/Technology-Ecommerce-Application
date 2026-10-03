package com.project.techstore.recommendation.service;

import com.project.techstore.behavior.service.BehaviorService;
import com.project.techstore.common.exception.AppException;
import com.project.techstore.common.exception.ErrorCode;
import com.project.techstore.order.repository.OrderItemRepository;
import com.project.techstore.product.dto.ProductResponse;
import com.project.techstore.product.entity.Product;
import com.project.techstore.product.entity.ProductStatus;
import com.project.techstore.product.repository.ProductRepository;
import com.project.techstore.recommendation.dto.RecommendationResponse;
import com.project.techstore.recommendation.dto.RecommendationResponse.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {
    private final ProductRepository products;
    private final OrderItemRepository orderItems;
    private final BehaviorService behaviors;
    private final AiRecommendationClient ai;

    @Transactional(readOnly = true)
    public List<ProductResponse> getBestSellers(int limit) {
        return orderedProducts(bestSellerIds(Set.of(), null, limit)).stream()
                .filter(this::available).map(ProductResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public RecommendationResponse forUser(Long userId, int limit) {
        var interactions = userId == null ? List.<com.project.techstore.behavior.dto.UserInteractionExportDto>of()
                : behaviors.getInteractionsForUser(userId);
        Set<Long> excluded = interactions.stream().map(i -> i.getProductId()).collect(Collectors.toSet());
        List<Item> result = new ArrayList<>();
        String fallbackReason = "no_history";
        if (!interactions.isEmpty()) {
            try {
                result.addAll(enrich(ai.forUser(interactions, Math.min(50, limit * 3)), excluded, null, limit));
                fallbackReason = result.size() < limit ? "insufficient_matches" : null;
            } catch (RuntimeException e) {
                log.warn("AI recommendation unavailable for user {}: {}", userId, e.getMessage());
                fallbackReason = "ai_unavailable";
            }
        }
        fill(result, excluded, null, limit);
        return response(result, fallbackReason);
    }

    @Transactional(readOnly = true)
    public RecommendationResponse similar(Long productId, int limit) {
        Product target = products.findWithDetailsById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        Long categoryId = target.getCategory().getId();
        Set<Long> excluded = new HashSet<>(Set.of(productId));
        List<Item> result = new ArrayList<>();
        String fallbackReason;
        try {
            result.addAll(enrich(ai.similar(productId, Math.min(50, limit * 3)), excluded, categoryId, limit));
            fallbackReason = result.size() < limit ? "insufficient_matches" : null;
        } catch (RuntimeException e) {
            log.warn("AI similar products unavailable for product {}: {}", productId, e.getMessage());
            fallbackReason = "ai_unavailable";
        }
        fill(result, excluded, categoryId, limit);
        return response(result, fallbackReason);
    }

    private List<Item> enrich(List<AiRecommendationClient.AiItem> candidates, Set<Long> excluded,
                              Long categoryId, int limit) {
        var ids = candidates.stream().filter(Objects::nonNull).map(AiRecommendationClient.AiItem::productId)
                .filter(Objects::nonNull).distinct().limit(50).toList();
        var byId = orderedProducts(ids).stream().collect(Collectors.toMap(Product::getId, Function.identity()));
        List<Item> result = new ArrayList<>();
        Set<Long> seen = new HashSet<>(excluded);
        for (var item : candidates) {
            if (item == null || item.score() == null || !Double.isFinite(item.score()) || item.score() <= 0
                    || item.score() > 1) continue;
            Product p = byId.get(item.productId());
            if (p == null || !available(p) || seen.contains(p.getId())
                    || (categoryId != null && !categoryId.equals(p.getCategory().getId()))) continue;
            seen.add(p.getId());
            result.add(new Item(ProductResponse.from(p), "content_based", item.score(), item.reason()));
            if (result.size() == limit) break;
        }
        return result;
    }

    private void fill(List<Item> result, Set<Long> excluded, Long categoryId, int limit) {
        Set<Long> seen = new HashSet<>(excluded);
        result.forEach(i -> seen.add(i.product().getId()));
        int remaining = limit - result.size();
        if (remaining <= 0) return;
        for (Product p : orderedProducts(bestSellerIds(seen, categoryId, remaining))) {
            if (available(p) && seen.add(p.getId())) {
                result.add(new Item(ProductResponse.from(p), "best_seller", null,
                        "Sản phẩm bán chạy từ các đơn hàng đã giao và thanh toán"));
            }
        }
        remaining = limit - result.size();
        if (remaining <= 0) return;
        // Không gắn nhãn bán chạy cho sản phẩm chưa có doanh số.
        for (Product p : products.findAvailableForRecommendations(nonEmptyIds(seen), categoryId,
                PageRequest.of(0, remaining))) {
            if (available(p) && seen.add(p.getId())) {
                result.add(new Item(ProductResponse.from(p), "catalog", null,
                        "Sản phẩm đang có sẵn để bạn khám phá"));
            }
        }
    }

    private RecommendationResponse response(List<Item> result, String fallbackReason) {
        String source = result.stream().anyMatch(i -> "content_based".equals(i.source())) ? "content_based"
                : result.stream().anyMatch(i -> "best_seller".equals(i.source())) ? "best_seller" : "catalog";
        return new RecommendationResponse(source, fallbackReason, List.copyOf(result));
    }

    private List<Long> bestSellerIds(Set<Long> excluded, Long categoryId, int limit) {
        return orderItems.findBestSellingProductIds(nonEmptyIds(excluded), categoryId, PageRequest.of(0, limit));
    }

    private Collection<Long> nonEmptyIds(Set<Long> ids) {
        return ids.isEmpty() ? List.of(-1L) : ids;
    }

    private List<Product> orderedProducts(List<Long> ids) {
        if (ids.isEmpty()) return List.of();
        var byId = products.findAllById(ids).stream().collect(Collectors.toMap(Product::getId, Function.identity()));
        return ids.stream().map(byId::get).filter(Objects::nonNull).toList();
    }

    private boolean available(Product p) {
        return p.getStatus() == ProductStatus.ACTIVE && p.getVariants().stream()
                .anyMatch(v -> "ACTIVE".equals(v.getStatus()) && v.getStock() != null && v.getStock() > 0);
    }
}
