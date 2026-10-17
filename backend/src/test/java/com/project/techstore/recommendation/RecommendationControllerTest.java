package com.project.techstore.recommendation;

import com.project.techstore.auth.security.JwtAuthenticationFilter;
import com.project.techstore.product.dto.ProductResponse;
import com.project.techstore.recommendation.controller.RecommendationController;
import com.project.techstore.recommendation.dto.RecommendationResponse;
import com.project.techstore.recommendation.service.RecommendationService;
import com.project.techstore.user.entity.Role;
import com.project.techstore.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecommendationController.class)
@AutoConfigureMockMvc(addFilters = false)
class RecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecommendationService recommendationService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private UserDetailsService userDetailsService;

    private User sampleUser;
    private RecommendationResponse sampleRecommendation;

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

        ProductResponse product = ProductResponse.builder()
                .id(100L)
                .name("MacBook Pro 16")
                .slug("macbook-pro-16")
                .minPrice(BigDecimal.valueOf(79990000))
                .build();

        RecommendationResponse.Item item = new RecommendationResponse.Item(
                product, "AI_MODEL", 0.95, "Best seller in laptops"
        );

        sampleRecommendation = new RecommendationResponse("AI_MODEL", null, List.of(item));
    }

    @Test
    void testForCurrentUserLoggedIn() throws Exception {
        when(recommendationService.forUser(1L, 8)).thenReturn(sampleRecommendation);

        mockMvc.perform(get("/api/v1/recommendations/me?limit=8").with(user(sampleUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.source").value("AI_MODEL"))
                .andExpect(jsonPath("$.data.recommendations[0].product.name").value("MacBook Pro 16"));

        verify(recommendationService).forUser(1L, 8);
    }

    @Test
    void testForCurrentUserAnonymous() throws Exception {
        SecurityContextHolder.clearContext();

        when(recommendationService.forUser(null, 8)).thenReturn(sampleRecommendation);

        mockMvc.perform(get("/api/v1/recommendations/me?limit=8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.source").value("AI_MODEL"));

        verify(recommendationService).forUser(null, 8);
    }

    @Test
    void testSimilarProducts() throws Exception {
        when(recommendationService.similar(100L, 4)).thenReturn(sampleRecommendation);

        mockMvc.perform(get("/api/v1/recommendations/similar/100?limit=4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.source").value("AI_MODEL"))
                .andExpect(jsonPath("$.data.recommendations[0].product.id").value(100));

        verify(recommendationService).similar(100L, 4);
    }
}
