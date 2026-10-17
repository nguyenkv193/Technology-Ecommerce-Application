package com.project.techstore.behavior;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.techstore.auth.security.JwtAuthenticationFilter;
import com.project.techstore.behavior.controller.BehaviorController;
import com.project.techstore.behavior.dto.RecommendationTrainingDataPage;
import com.project.techstore.behavior.dto.TrackBehaviorRequest;
import com.project.techstore.behavior.dto.UserBehaviorResponse;
import com.project.techstore.behavior.entity.BehaviorType;
import com.project.techstore.behavior.service.AiServiceKeyValidator;
import com.project.techstore.behavior.service.BehaviorService;
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

@WebMvcTest(BehaviorController.class)
@AutoConfigureMockMvc(addFilters = false)
class BehaviorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BehaviorService behaviorService;

    @MockBean
    private AiServiceKeyValidator aiServiceKeyValidator;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private UserDetailsService userDetailsService;

    private User sampleUser;
    private UserBehaviorResponse sampleResponse;

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

        sampleResponse = UserBehaviorResponse.builder()
                .id(1L)
                .userId(1L)
                .productId(100L)
                .actionType("VIEW")
                .actionValue("120s")
                .sessionId("sess_123")
                .createdAt(Instant.now())
                .build();
    }

    @Test
    void testTrackBehaviorLoggedInUser() throws Exception {
        TrackBehaviorRequest request = TrackBehaviorRequest.builder()
                .productId(100L)
                .actionType(BehaviorType.VIEW)
                .actionValue("120s")
                .sessionId("sess_123")
                .build();

        when(behaviorService.track(eq(1L), any(TrackBehaviorRequest.class))).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/v1/behaviors/track")
                        .with(user(sampleUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.actionType").value("VIEW"));

        verify(behaviorService).track(eq(1L), any(TrackBehaviorRequest.class));
    }

    @Test
    void testTrackBehaviorAnonymousUser() throws Exception {
        SecurityContextHolder.clearContext();

        TrackBehaviorRequest request = TrackBehaviorRequest.builder()
                .productId(100L)
                .actionType(BehaviorType.VIEW)
                .sessionId("sess_anon")
                .build();

        UserBehaviorResponse anonResponse = UserBehaviorResponse.builder()
                .id(2L)
                .productId(100L)
                .actionType("VIEW")
                .sessionId("sess_anon")
                .createdAt(Instant.now())
                .build();

        when(behaviorService.track(eq(null), any(TrackBehaviorRequest.class))).thenReturn(anonResponse);

        mockMvc.perform(post("/api/v1/behaviors/track")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.sessionId").value("sess_anon"));

        verify(behaviorService).track(eq(null), any(TrackBehaviorRequest.class));
    }

    @Test
    void testTrackBehaviorValidationFailed() throws Exception {
        TrackBehaviorRequest invalidRequest = TrackBehaviorRequest.builder()
                .actionType(null)
                .build();

        mockMvc.perform(post("/api/v1/behaviors/track")
                        .with(user(sampleUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verifyNoInteractions(behaviorService);
    }

    @Test
    void testGetMyBehaviors() throws Exception {
        when(behaviorService.getUserRecentBehaviors(1L)).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/v1/behaviors/my").with(user(sampleUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].actionType").value("VIEW"));

        verify(behaviorService).getUserRecentBehaviors(1L);
    }

    @Test
    void testRecommendationTrainingDataAuthorized() throws Exception {
        when(aiServiceKeyValidator.matches("valid-key")).thenReturn(true);

        RecommendationTrainingDataPage pageData = new RecommendationTrainingDataPage(
                List.of(), 0, 10, true, Instant.now()
        );

        when(behaviorService.getTrainingInteractions(eq(0), eq(10), any(Instant.class)))
                .thenReturn(pageData);

        mockMvc.perform(get("/api/v1/behaviors/recommendation-training-data")
                        .header("X-AI-Service-Key", "valid-key")
                        .param("page", "0")
                        .param("size", "10")
                        .param("asOf", "2026-10-17T10:00:00Z"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.page").value(0));

        verify(behaviorService).getTrainingInteractions(eq(0), eq(10), any(Instant.class));
    }

    @Test
    void testRecommendationTrainingDataUnauthorized() throws Exception {
        when(aiServiceKeyValidator.matches("wrong-key")).thenReturn(false);

        mockMvc.perform(get("/api/v1/behaviors/recommendation-training-data")
                        .header("X-AI-Service-Key", "wrong-key")
                        .param("page", "0")
                        .param("size", "10")
                        .param("asOf", "2026-10-17T10:00:00Z"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(behaviorService);
    }
}
