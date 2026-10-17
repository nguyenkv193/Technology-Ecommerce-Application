package com.project.techstore.review;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.techstore.auth.security.JwtAuthenticationFilter;
import com.project.techstore.common.dto.PageResponse;
import com.project.techstore.review.controller.ReviewController;
import com.project.techstore.review.dto.CreateReviewRequest;
import com.project.techstore.review.dto.ProductReviewSummaryResponse;
import com.project.techstore.review.dto.ReviewResponse;
import com.project.techstore.review.service.ReviewService;
import com.project.techstore.user.entity.Role;
import com.project.techstore.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private UserDetailsService userDetailsService;

    private User sampleUser;
    private ReviewResponse sampleReview;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .email("user@techstore.vn")
                .fullName("Nguyen Van A")
                .role(Role.USER)
                .build();
        sampleUser.setId(1L);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(sampleUser, null, sampleUser.getAuthorities())
        );

        sampleReview = ReviewResponse.builder()
                .id(1L)
                .productId(100L)
                .userId(1L)
                .userFullName("Nguyen Van A")
                .rating(5)
                .comment("San pham tuyet voi!")
                .createdAt(Instant.now())
                .build();
    }

    @Test
    void testGetProductReviews() throws Exception {
        PageResponse<ReviewResponse> pageResponse = PageResponse.<ReviewResponse>builder()
                .content(List.of(sampleReview))
                .page(0)
                .size(10)
                .totalElements(1)
                .totalPages(1)
                .last(true)
                .build();

        when(reviewService.getProductReviews(100L, 0, 10)).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/reviews/product/100?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].rating").value(5))
                .andExpect(jsonPath("$.data.totalElements").value(1));

        verify(reviewService).getProductReviews(100L, 0, 10);
    }

    @Test
    void testGetProductReviewSummary() throws Exception {
        ProductReviewSummaryResponse summary = ProductReviewSummaryResponse.builder()
                .averageRating(4.8)
                .totalReviews(20L)
                .reviews(PageResponse.<ReviewResponse>builder()
                        .content(List.of(sampleReview))
                        .page(0)
                        .size(10)
                        .totalElements(1)
                        .totalPages(1)
                        .last(true)
                        .build())
                .build();

        when(reviewService.getProductReviewSummary(100L, 0, 10)).thenReturn(summary);

        mockMvc.perform(get("/api/v1/reviews/product/100/summary?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.averageRating").value(4.8))
                .andExpect(jsonPath("$.data.totalReviews").value(20));

        verify(reviewService).getProductReviewSummary(100L, 0, 10);
    }

    @Test
    void testSubmitReviewSuccess() throws Exception {
        CreateReviewRequest request = CreateReviewRequest.builder()
                .productId(100L)
                .rating(5)
                .comment("San pham tuyet voi!")
                .build();

        when(reviewService.createOrUpdateReview(eq(1L), any(CreateReviewRequest.class)))
                .thenReturn(sampleReview);

        mockMvc.perform(post("/api/v1/reviews")
                        .with(user(sampleUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Đánh giá sản phẩm thành công"))
                .andExpect(jsonPath("$.data.rating").value(5));

        verify(reviewService).createOrUpdateReview(eq(1L), any(CreateReviewRequest.class));
    }

    @Test
    void testSubmitReviewValidationFailed() throws Exception {
        CreateReviewRequest invalidRequest = CreateReviewRequest.builder()
                .productId(null)
                .rating(6) // max is 5
                .build();

        mockMvc.perform(post("/api/v1/reviews")
                        .with(user(sampleUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verifyNoInteractions(reviewService);
    }
}
