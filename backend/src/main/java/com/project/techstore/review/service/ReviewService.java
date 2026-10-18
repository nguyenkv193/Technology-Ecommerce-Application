package com.project.techstore.review.service;

import com.project.techstore.common.dto.PageResponse;
import com.project.techstore.common.exception.AppException;
import com.project.techstore.common.exception.ErrorCode;
import com.project.techstore.product.entity.Product;
import com.project.techstore.product.service.ProductService;
import com.project.techstore.review.dto.CreateReviewRequest;
import com.project.techstore.review.dto.ProductReviewSummaryResponse;
import com.project.techstore.review.dto.RatingBreakdownItem;
import com.project.techstore.review.dto.ReviewResponse;
import com.project.techstore.review.entity.ProductReview;
import com.project.techstore.review.repository.ProductReviewRepository;
import com.project.techstore.user.entity.User;
import com.project.techstore.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ProductReviewRepository productReviewRepository;
    private final ProductService productService;
    private final UserRepository userRepository;

    @Transactional
    public ReviewResponse createOrUpdateReview(Long userId, CreateReviewRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Product product = productService.getProductEntity(request.getProductId());

        Optional<ProductReview> existingOpt = productReviewRepository.findByUserIdAndProductId(userId, request.getProductId());

        ProductReview review;
        if (existingOpt.isPresent()) {
            review = existingOpt.get();
            review.setRating(request.getRating());
            review.setComment(request.getComment());
            log.info("Người dùng id={} đã cập nhật đánh giá sản phẩm id={} ({} sao)", userId, product.getId(), request.getRating());
        } else {
            review = ProductReview.builder()
                    .user(user)
                    .product(product)
                    .rating(request.getRating())
                    .comment(request.getComment())
                    .build();
            log.info("Người dùng id={} đã gửi đánh giá mới cho sản phẩm id={} ({} sao)", userId, product.getId(), request.getRating());
        }

        ProductReview saved = productReviewRepository.save(review);
        return ReviewResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> getProductReviews(Long productId, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size));
        Page<ProductReview> reviewPage = productReviewRepository.findByProductIdOrderByCreatedAtDesc(productId, pageable);
        return PageResponse.of(reviewPage.map(ReviewResponse::from));
    }

    @Transactional(readOnly = true)
    public ProductReviewSummaryResponse getProductReviewSummary(Long productId, int page, int size) {
        Double avg = productReviewRepository.getAverageRatingByProductId(productId);
        long total = productReviewRepository.countByProductId(productId);

        Double roundedAvg = (avg != null)
                ? BigDecimal.valueOf(avg).setScale(1, RoundingMode.HALF_UP).doubleValue()
                : 0.0;

        PageResponse<ReviewResponse> reviews = getProductReviews(productId, page, size);
        Map<Integer, Long> countsByRating = new HashMap<>();
        for (Object[] row : productReviewRepository.countRatingsByProductId(productId)) {
            countsByRating.put(((Number) row[0]).intValue(), ((Number) row[1]).longValue());
        }
        var ratingBreakdown = new ArrayList<RatingBreakdownItem>();
        for (int stars = 5; stars >= 1; stars--) {
            ratingBreakdown.add(new RatingBreakdownItem(stars, countsByRating.getOrDefault(stars, 0L)));
        }

        return ProductReviewSummaryResponse.builder()
                .productId(productId)
                .averageRating(roundedAvg)
                .totalReviews(total)
                .ratingBreakdown(ratingBreakdown)
                .reviews(reviews)
                .build();
    }
}
