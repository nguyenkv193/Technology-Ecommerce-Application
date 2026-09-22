package com.project.techstore.behavior;

import com.project.techstore.behavior.dto.TrackBehaviorRequest;
import com.project.techstore.behavior.dto.UserBehaviorResponse;
import com.project.techstore.behavior.dto.UserInteractionExportDto;
import com.project.techstore.behavior.entity.BehaviorType;
import com.project.techstore.behavior.entity.UserBehavior;
import com.project.techstore.behavior.repository.UserBehaviorRepository;
import com.project.techstore.behavior.service.BehaviorService;
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
    void testExportInteractionsForAi() {
        Object[] mockRow = new Object[]{1L, 50L, 8.5, 4L, Instant.now()};
        when(userBehaviorRepository.aggregateUserProductInteractions()).thenReturn(List.<Object[]>of(mockRow));

        List<UserInteractionExportDto> matrix = behaviorService.exportInteractionsForAi();

        assertNotNull(matrix);
        assertEquals(1, matrix.size());
        assertEquals(1L, matrix.get(0).getUserId());
        assertEquals(50L, matrix.get(0).getProductId());
        assertEquals(8.5, matrix.get(0).getScore());
        assertEquals(4L, matrix.get(0).getInteractionCount());
    }
}
