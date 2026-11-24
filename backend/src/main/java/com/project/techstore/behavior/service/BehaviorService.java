package com.project.techstore.behavior.service;

import com.project.techstore.behavior.dto.BehaviorAnalyticsResponse;
import com.project.techstore.behavior.dto.TrackBehaviorRequest;
import com.project.techstore.behavior.dto.RecommendationTrainingDataPage;
import com.project.techstore.behavior.dto.TrendingKeywordResponse;
import com.project.techstore.behavior.dto.UserBehaviorResponse;
import com.project.techstore.behavior.dto.UserInteractionExportDto;
import com.project.techstore.behavior.entity.BehaviorType;
import com.project.techstore.behavior.entity.UserBehavior;
import com.project.techstore.behavior.repository.UserBehaviorRepository;
import com.project.techstore.common.exception.AppException;
import com.project.techstore.common.exception.ErrorCode;
import com.project.techstore.product.dto.ProductResponse;
import com.project.techstore.product.entity.Product;
import com.project.techstore.product.repository.ProductRepository;
import com.project.techstore.user.entity.User;
import com.project.techstore.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class BehaviorService {

    private final UserBehaviorRepository userBehaviorRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public UserBehaviorResponse track(Long userId, TrackBehaviorRequest request) {
        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId).orElse(null);
        }

        Product product = null;
        if (request.getProductId() != null) {
            product = productRepository.findById(request.getProductId()).orElse(null);
        }

        UserBehavior behavior = UserBehavior.builder()
                .user(user)
                .sessionId(request.getSessionId())
                .product(product)
                .actionType(request.getActionType())
                .actionValue(request.getActionValue())
                .build();

        UserBehavior saved = userBehaviorRepository.save(behavior);
        log.debug("Ghi nhận hành vi: userId={}, action={}, productId={}", userId, request.getActionType(), request.getProductId());
        return UserBehaviorResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<UserBehaviorResponse> getUserRecentBehaviors(Long userId) {
        return userBehaviorRepository.findTop50ByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(UserBehaviorResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TrendingKeywordResponse> getTrendingKeywords(int limit, int days) {
        Instant since = Instant.now().minus(Duration.ofDays(Math.max(1, days)));
        List<Object[]> rows = new ArrayList<>(userBehaviorRepository.findTopSearchKeywordsSince(since, PageRequest.of(0, limit)));

        // Nếu trong khoảng N ngày chưa đủ từ khóa, bổ sung từ khóa tìm kiếm all-time
        if (rows.size() < limit) {
            List<Object[]> allTimeRows = userBehaviorRepository.findAllTimeTopSearchKeywords(PageRequest.of(0, limit));
            Set<String> seen = new HashSet<>();
            for (Object[] r : rows) {
                seen.add(r[0].toString().trim().toLowerCase());
            }
            for (Object[] r : allTimeRows) {
                if (!seen.contains(r[0].toString().trim().toLowerCase())) {
                    rows.add(r);
                    seen.add(r[0].toString().trim().toLowerCase());
                    if (rows.size() >= limit) break;
                }
            }
        }

        List<TrendingKeywordResponse> result = new ArrayList<>();
        Set<String> addedKeywords = new HashSet<>();

        for (Object[] r : rows) {
            String kw = r[0].toString().trim();
            long count = ((Number) r[1]).longValue();
            if (!kw.isEmpty() && addedKeywords.add(kw.toLowerCase())) {
                String encodedKw = URLEncoder.encode(kw, StandardCharsets.UTF_8);
                result.add(TrendingKeywordResponse.builder()
                        .keyword(kw)
                        .link("/products?keyword=" + encodedKw)
                        .searchCount(count)
                        .build());
            }
        }

        // Fallback thêm các từ khóa mặc định nếu hệ thống còn ít dữ liệu tìm kiếm
        List<String> defaultKeywords = List.of(
                "iPhone 16 Pro Max", "Laptop AI 2026", "MacBook Air M3",
                "Màn Hình 144Hz", "PC Gaming ROG", "Bàn Phím Cơ",
                "Tai Nghe ANC", "Back to School 50%"
        );

        for (String defKw : defaultKeywords) {
            if (result.size() >= limit) break;
            if (addedKeywords.add(defKw.toLowerCase())) {
                String encodedKw = URLEncoder.encode(defKw, StandardCharsets.UTF_8);
                result.add(TrendingKeywordResponse.builder()
                        .keyword(defKw)
                        .link("/products?keyword=" + encodedKw)
                        .searchCount(0L)
                        .build());
            }
        }

        return result;
    }

    @Transactional(readOnly = true)
    public List<UserInteractionExportDto> getInteractionsForUser(Long userId) {
        Instant asOf = Instant.now();
        List<Object[]> rows = userBehaviorRepository.aggregateUserProductInteractions(userId);
        return rows.stream().map(row -> UserInteractionExportDto.builder()
                .userId(((Number) row[0]).longValue())
                .productId(((Number) row[1]).longValue())
                .score(preferenceScore(((Number) row[2]).doubleValue(), asInstant(row[4]), asOf))
                .interactionCount(((Number) row[3]).longValue())
                .lastInteractedAt(asInstant(row[4]))
                .build()
        ).filter(interaction -> interaction.getScore() > 0).toList();
    }

    @Transactional(readOnly = true)
    public RecommendationTrainingDataPage getTrainingInteractions(int page, int size, Instant asOf) {
        List<Object[]> rows = userBehaviorRepository.aggregateTrainingInteractions(asOf, PageRequest.of(page, size));
        List<UserInteractionExportDto> interactions = rows.stream().map(row -> {
            Instant lastInteractedAt = asInstant(row[4]);
            return UserInteractionExportDto.builder()
                    .userId(((Number) row[0]).longValue())
                    .productId(((Number) row[1]).longValue())
                    .score(preferenceScore(((Number) row[2]).doubleValue(), lastInteractedAt, asOf))
                    .interactionCount(((Number) row[3]).longValue())
                    .lastInteractedAt(lastInteractedAt)
                    .build();
        }).filter(interaction -> interaction.getScore() > 0).toList();

        return new RecommendationTrainingDataPage(interactions, page, size, rows.size() < size, asOf);
    }

    private double preferenceScore(double rawScore, Instant lastInteractedAt, Instant referenceTime) {
        if (rawScore <= 0 || lastInteractedAt == null) {
            return 0;
        }
        double ageInDays = Math.max(0, Duration.between(lastInteractedAt, referenceTime).toSeconds() / 86_400.0);
        return Math.log1p(rawScore) * Math.pow(0.5, ageInDays / 30.0);
    }

    private Instant asInstant(Object value) {
        if (value instanceof Instant instant) {
            return instant;
        }
        if (value instanceof java.sql.Timestamp timestamp) {
            return timestamp.toInstant();
        }
        throw new IllegalStateException("Unsupported interaction timestamp type");
    }

    @Transactional(readOnly = true)
    public BehaviorAnalyticsResponse getAdminAnalytics(int days, int limit) {
        return getAdminAnalytics(days, limit, null, null);
    }

    @Transactional(readOnly = true)
    public BehaviorAnalyticsResponse getAdminAnalytics(int days, int limit, LocalDate from, LocalDate to) {
        int safeLimit = Math.max(1, Math.min(limit, 50));

        ZoneId vnZone = ZoneId.of("Asia/Ho_Chi_Minh");
        LocalDate startDate;
        LocalDate endDate;
        int safeDays;

        if (from != null || to != null) {
            LocalDate today = LocalDate.now(vnZone);
            endDate = to != null ? to : today;
            startDate = from != null ? from : endDate.minusDays(Math.max(1, Math.min(days, 90)) - 1L);
            if (startDate.isAfter(endDate)) {
                throw new AppException(ErrorCode.INVALID_REQUEST, "Ngày bắt đầu không được lớn hơn ngày kết thúc");
            }
            long spanDays = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
            if (spanDays > 90) {
                throw new AppException(ErrorCode.INVALID_REQUEST, "Khoảng thời gian thống kê tối đa là 90 ngày");
            }
            safeDays = (int) spanDays;
        } else {
            safeDays = Math.max(1, Math.min(days, 90));
            endDate = LocalDate.now(vnZone);
            startDate = endDate.minusDays(safeDays - 1L);
        }

        Instant since = startDate.atStartOfDay(vnZone).toInstant();
        Instant until = endDate.plusDays(1).atStartOfDay(vnZone).toInstant();

        // 1. Summary counts
        long totalViews = 0;
        long totalWishlists = 0;
        long totalAddToCart = 0;
        long totalPurchases = 0;
        long totalSearches = 0;

        for (Object[] row : userBehaviorRepository.countByActionTypeBetween(since, until)) {
            BehaviorType type = (BehaviorType) row[0];
            long count = ((Number) row[1]).longValue();
            switch (type) {
                case VIEW -> totalViews = count;
                case WISHLIST -> totalWishlists = count;
                case ADD_TO_CART -> totalAddToCart = count;
                case PURCHASE -> totalPurchases = count;
                case SEARCH -> totalSearches = count;
                default -> { }
            }
        }

        // 2. Daily series (fill all days in range)
        Map<String, BehaviorAnalyticsResponse.DailyInteractionPoint> dailyMap = new LinkedHashMap<>();
        for (int i = 0; i < safeDays; i++) {
            String dateStr = startDate.plusDays(i).toString();
            dailyMap.put(dateStr, BehaviorAnalyticsResponse.DailyInteractionPoint.builder()
                    .date(dateStr)
                    .views(0)
                    .wishlists(0)
                    .addToCarts(0)
                    .purchases(0)
                    .build());
        }

        for (Object[] row : userBehaviorRepository.findDailyActionCountsBetween(since, until)) {
            String dayStr = String.valueOf(row[0]);
            String actionStr = String.valueOf(row[1]);
            long count = ((Number) row[2]).longValue();
            BehaviorAnalyticsResponse.DailyInteractionPoint point = dailyMap.get(dayStr);
            if (point == null) continue;
            switch (actionStr) {
                case "VIEW" -> point.setViews(count);
                case "WISHLIST" -> point.setWishlists(count);
                case "ADD_TO_CART" -> point.setAddToCarts(count);
                case "PURCHASE" -> point.setPurchases(count);
                default -> { }
            }
        }

        // 3. Top interacted products
        List<Object[]> topRows = userBehaviorRepository.findTopInteractedProductsBetween(since, until, PageRequest.of(0, safeLimit));
        List<Long> productIds = topRows.stream().map(r -> ((Number) r[0]).longValue()).toList();
        Map<Long, ProductResponse> productMap = new HashMap<>();
        if (!productIds.isEmpty()) {
            for (Product product : productRepository.findAllById(productIds)) {
                ProductResponse dto = ProductResponse.from(product);
                if (dto != null) {
                    productMap.put(dto.getId(), dto);
                }
            }
        }

        List<BehaviorAnalyticsResponse.TopInteractedProduct> topProducts = new ArrayList<>();
        for (Object[] row : topRows) {
            Long productId = ((Number) row[0]).longValue();
            ProductResponse prod = productMap.get(productId);
            if (prod == null) continue;
            topProducts.add(BehaviorAnalyticsResponse.TopInteractedProduct.builder()
                    .productId(productId)
                    .productName(prod.getName())
                    .slug(prod.getSlug())
                    .categoryName(prod.getCategoryName())
                    .thumbnailUrl(prod.getThumbnailUrl())
                    .minPrice(prod.getMinPrice())
                    .viewCount(((Number) row[1]).longValue())
                    .wishlistCount(((Number) row[2]).longValue())
                    .addToCartCount(((Number) row[3]).longValue())
                    .purchaseCount(((Number) row[4]).longValue())
                    .interactionScore(((Number) row[5]).doubleValue())
                    .build());
        }

        return BehaviorAnalyticsResponse.builder()
                .days(safeDays)
                .summary(BehaviorAnalyticsResponse.InteractionSummary.builder()
                        .totalViews(totalViews)
                        .totalWishlists(totalWishlists)
                        .totalAddToCart(totalAddToCart)
                        .totalPurchases(totalPurchases)
                        .totalSearches(totalSearches)
                        .build())
                .dailySeries(new ArrayList<>(dailyMap.values()))
                .topProducts(topProducts)
                .build();
    }
}
