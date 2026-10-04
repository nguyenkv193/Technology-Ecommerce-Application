package com.project.techstore.behavior.service;

import com.project.techstore.behavior.dto.TrackBehaviorRequest;
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
        List<Object[]> rows = userBehaviorRepository.aggregateUserProductInteractions(userId);
        return rows.stream().map(row -> UserInteractionExportDto.builder()
                .userId(((Number) row[0]).longValue())
                .productId(((Number) row[1]).longValue())
                .score(((Number) row[2]).doubleValue())
                .interactionCount(((Number) row[3]).longValue())
                .lastInteractedAt((Instant) row[4])
                .build()
        ).toList();
    }
}
