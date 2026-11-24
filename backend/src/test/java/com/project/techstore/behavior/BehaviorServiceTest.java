package com.project.techstore.behavior;

import com.project.techstore.behavior.dto.BehaviorAnalyticsResponse;
import com.project.techstore.behavior.dto.TrackBehaviorRequest;
import com.project.techstore.behavior.dto.UserBehaviorResponse;
import com.project.techstore.behavior.dto.UserInteractionExportDto;
import com.project.techstore.behavior.entity.BehaviorType;
import com.project.techstore.behavior.entity.UserBehavior;
import com.project.techstore.behavior.repository.UserBehaviorRepository;
import com.project.techstore.behavior.service.BehaviorService;
import com.project.techstore.common.exception.AppException;
import com.project.techstore.common.exception.ErrorCode;
import com.project.techstore.product.entity.Product;
import com.project.techstore.product.repository.ProductRepository;
import com.project.techstore.user.entity.User;
import com.project.techstore.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BehaviorServiceTest {

    @Mock
    private UserBehaviorRepository userBehaviorRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private BehaviorService behaviorService;

    private User sampleUser;
    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder().email("tracker@example.com").build();
        sampleUser.setId(1L);

        sampleProduct = Product.builder().name("Dell XPS 13").slug("dell-xps-13").build();
        sampleProduct.setId(50L);
    }

    @Test
    void testTrackBehaviorSuccess() {
        TrackBehaviorRequest request = TrackBehaviorRequest.builder()
                .productId(50L)
                .actionType(BehaviorType.VIEW)
                .actionValue("dwell_time=45s")
                .sessionId("sess_123")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(productRepository.findById(50L)).thenReturn(Optional.of(sampleProduct));
        when(userBehaviorRepository.save(any(UserBehavior.class))).thenAnswer(inv -> {
            UserBehavior b = inv.getArgument(0);
            b.setId(1001L);
            return b;
        });

        UserBehaviorResponse response = behaviorService.track(1L, request);

        assertNotNull(response);
        assertEquals(1001L, response.getId());
        assertEquals("VIEW", response.getActionType());
        assertEquals("Dell XPS 13", response.getProductName());
        verify(userBehaviorRepository, times(1)).save(any(UserBehavior.class));
    }

    @Test
    void testInteractionsAreScopedToCurrentUser() {
        Object[] mockRow = new Object[]{1L, 50L, 8.5, 4L, Instant.now()};
        when(userBehaviorRepository.aggregateUserProductInteractions(1L)).thenReturn(List.<Object[]>of(mockRow));

        List<UserInteractionExportDto> matrix = behaviorService.getInteractionsForUser(1L);

        assertNotNull(matrix);
        assertEquals(1, matrix.size());
        assertEquals(1L, matrix.get(0).getUserId());
        assertEquals(50L, matrix.get(0).getProductId());
        assertEquals(Math.log1p(8.5), matrix.get(0).getScore(), 0.001);
        assertEquals(4L, matrix.get(0).getInteractionCount());
        verify(userBehaviorRepository).aggregateUserProductInteractions(1L);
    }

    @Test
    void testGetAdminAnalyticsWithFromToRange() {
        LocalDate from = LocalDate.of(2026, 11, 1);
        LocalDate to = LocalDate.of(2026, 11, 5);

        when(userBehaviorRepository.countByActionTypeBetween(any(), any()))
                .thenReturn(List.<Object[]>of(new Object[]{BehaviorType.VIEW, 25L}));
        when(userBehaviorRepository.findDailyActionCountsBetween(any(), any()))
                .thenReturn(Collections.emptyList());
        when(userBehaviorRepository.findTopInteractedProductsBetween(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        BehaviorAnalyticsResponse res = behaviorService.getAdminAnalytics(14, 10, from, to);

        assertEquals(5, res.getDays());
        assertEquals(5, res.getDailySeries().size());
        assertEquals("2026-11-01", res.getDailySeries().get(0).getDate());
        assertEquals("2026-11-05", res.getDailySeries().get(4).getDate());
        assertEquals(25L, res.getSummary().getTotalViews());
    }

    @Test
    void testGetAdminAnalyticsRejectsInvalidDateRange() {
        LocalDate from = LocalDate.of(2026, 11, 10);
        LocalDate to = LocalDate.of(2026, 11, 5);

        AppException ex = assertThrows(AppException.class,
                () -> behaviorService.getAdminAnalytics(14, 10, from, to));
        assertEquals(ErrorCode.INVALID_REQUEST, ex.getErrorCode());
    }
}
