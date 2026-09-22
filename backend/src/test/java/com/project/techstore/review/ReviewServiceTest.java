package com.project.techstore.review;

import com.project.techstore.product.entity.Product;
import com.project.techstore.product.service.ProductService;
import com.project.techstore.review.dto.CreateReviewRequest;
import com.project.techstore.review.dto.ReviewResponse;
import com.project.techstore.review.entity.ProductReview;
import com.project.techstore.review.repository.ProductReviewRepository;
import com.project.techstore.review.service.ReviewService;
import com.project.techstore.user.entity.User;
import com.project.techstore.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ProductReviewRepository productReviewRepository;

    @Mock
    private ProductService productService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReviewService reviewService;

    private User sampleUser;
    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder().email("reviewer@example.com").fullName("Nguyễn Reviewer").build();
        sampleUser.setId(1L);

        sampleProduct = Product.builder().name("MacBook Pro").slug("macbook-pro").build();
        sampleProduct.setId(10L);
    }

    @Test
    void testCreateReviewSuccess() {
        CreateReviewRequest request = CreateReviewRequest.builder()
                .productId(10L)
                .rating(5)
                .comment("Sản phẩm rất tốt!")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(productService.getProductEntity(10L)).thenReturn(sampleProduct);
        when(productReviewRepository.findByUserIdAndProductId(1L, 10L)).thenReturn(Optional.empty());
        when(productReviewRepository.save(any(ProductReview.class))).thenAnswer(inv -> {
            ProductReview r = inv.getArgument(0);
            r.setId(100L);
            return r;
        });

        ReviewResponse response = reviewService.createOrUpdateReview(1L, request);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(5, response.getRating());
        assertEquals("Sản phẩm rất tốt!", response.getComment());
        assertEquals("Nguyễn Reviewer", response.getUserFullName());
        verify(productReviewRepository, times(1)).save(any(ProductReview.class));
    }
}
