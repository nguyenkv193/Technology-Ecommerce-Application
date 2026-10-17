package com.project.techstore.behavior.service;

import com.project.techstore.behavior.dto.TrackBehaviorRequest;
import com.project.techstore.behavior.dto.RecommendationTrainingDataPage;
import com.project.techstore.behavior.dto.UserBehaviorResponse;
import com.project.techstore.behavior.dto.UserInteractionExportDto;
import com.project.techstore.behavior.entity.UserBehavior;
import com.project.techstore.behavior.repository.UserBehaviorRepository;
import com.project.techstore.product.entity.Product;
import com.project.techstore.product.repository.ProductRepository;
import com.project.techstore.user.entity.User;
import com.project.techstore.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

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
}
