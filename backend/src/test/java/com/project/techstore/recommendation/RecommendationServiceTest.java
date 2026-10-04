package com.project.techstore.recommendation;

import com.project.techstore.behavior.dto.UserInteractionExportDto;
import com.project.techstore.behavior.service.BehaviorService;
import com.project.techstore.category.entity.Category;
import com.project.techstore.brand.entity.Brand;
import com.project.techstore.order.repository.OrderItemRepository;
import com.project.techstore.product.entity.*;
import com.project.techstore.product.repository.ProductRepository;
import com.project.techstore.recommendation.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {
    @Mock ProductRepository products;
    @Mock OrderItemRepository orderItems;
    @Mock BehaviorService behaviors;
    @Mock AiRecommendationClient ai;
    @InjectMocks RecommendationService service;

    private Product product(long id, int stock) {
        var category = Category.builder().name("Laptop").slug("laptop").build();
        category.setId(10L);
        var brand = Brand.builder().name("Apple").slug("apple").build();
        var p = Product.builder().name("Product " + id).slug("product-" + id).category(category).brand(brand).build();
        p.setId(id);
        p.addVariant(ProductVariant.builder().sku("sku-" + id).name("Variant").price(BigDecimal.TEN).stock(stock).build());
        return p;
    }

    private List<UserInteractionExportDto> history() {
        return List.of(UserInteractionExportDto.builder().userId(7L).productId(1L).score(4.0).build());
    }

    @Test
    void guestUsesBestSellerWithoutCallingAiOrAnyUsersHistory() {
        when(orderItems.findBestSellingProductIds(anyCollection(), isNull(), any())).thenReturn(List.of(3L, 2L));
        when(products.findAllById(List.of(3L, 2L))).thenReturn(List.of(product(2, 4), product(3, 5)));
        var response = service.forUser(null, 2);
        assertEquals("best_seller", response.source());
        assertEquals("no_history", response.fallbackReason());
        assertEquals(List.of(3L, 2L), response.recommendations().stream().map(i -> i.product().getId()).toList());
        verifyNoInteractions(ai, behaviors);
    }

    @Test
    void newAuthenticatedUserAlsoUsesBestSeller() {
        when(behaviors.getInteractionsForUser(7L)).thenReturn(List.of());
        when(orderItems.findBestSellingProductIds(anyCollection(), isNull(), any())).thenReturn(List.of(2L));
        when(products.findAllById(List.of(2L))).thenReturn(List.of(product(2, 1)));
        assertEquals("best_seller", service.forUser(7L, 1).source());
        verifyNoInteractions(ai);
    }

    @Test
    void aiFailureFallsBackAndExcludesInteractedProducts() {
        when(behaviors.getInteractionsForUser(7L)).thenReturn(history());
        when(ai.forUser(anyList(), anyInt())).thenThrow(new IllegalStateException("503 not ready"));
        when(orderItems.findBestSellingProductIds(anyCollection(), isNull(), any())).thenReturn(List.of(2L));
        when(products.findAllById(List.of(2L))).thenReturn(List.of(product(2, 1)));
        var response = service.forUser(7L, 1);
        assertEquals("ai_unavailable", response.fallbackReason());
        assertEquals("best_seller", response.source());
        verify(orderItems).findBestSellingProductIds(argThat(ids -> ids.contains(1L)), isNull(), any());
        assertEquals(2L, response.recommendations().getFirst().product().getId());
    }

    @Test
    void aiResultsAreRevalidatedAndFillersDoNotRepeatSeenOrRecommendedProducts() {
        when(behaviors.getInteractionsForUser(7L)).thenReturn(history());
        when(ai.forUser(anyList(), anyInt())).thenReturn(List.of(
                new AiRecommendationClient.AiItem(1L, .9, "seen"),
                new AiRecommendationClient.AiItem(2L, .8, "match"),
                new AiRecommendationClient.AiItem(2L, .7, "duplicate"),
                new AiRecommendationClient.AiItem(3L, .6, "out of stock"),
                new AiRecommendationClient.AiItem(999L, .5, "deleted"),
                new AiRecommendationClient.AiItem(5L, Double.NaN, "invalid")));
        when(products.findAllById(List.of(1L, 2L, 3L, 999L, 5L)))
                .thenReturn(List.of(product(1, 1), product(2, 1), product(3, 0), product(5, 1)));
        when(orderItems.findBestSellingProductIds(anyCollection(), isNull(), any())).thenReturn(List.of(4L));
        when(products.findAllById(List.of(4L))).thenReturn(List.of(product(4, 1)));
        var response = service.forUser(7L, 2);
        assertEquals("content_based", response.source());
        assertEquals(List.of(2L, 4L), response.recommendations().stream().map(i -> i.product().getId()).toList());
        assertEquals(List.of("content_based", "best_seller"), response.recommendations().stream().map(i -> i.source()).toList());
        verify(orderItems).findBestSellingProductIds(argThat(ids -> ids.containsAll(Set.of(1L, 2L))), isNull(), any());
    }

    @Test
    void noSalesUsesRealCatalogWithHonestSourceAndNoInventedScore() {
        when(orderItems.findBestSellingProductIds(anyCollection(), isNull(), any())).thenReturn(List.of());
        when(products.findAvailableForRecommendations(anyCollection(), isNull(), any())).thenReturn(List.of(product(6, 2)));
        var response = service.forUser(null, 4);
        assertEquals("catalog", response.source());
        assertEquals(1, response.recommendations().size());
        assertNull(response.recommendations().getFirst().score());
    }

    @Test
    void similarFallbackRemainsInTargetsCategoryAndExcludesTarget() {
        when(products.findWithDetailsById(1L)).thenReturn(Optional.of(product(1, 1)));
        when(ai.similar(anyLong(), anyInt())).thenThrow(new IllegalStateException("timeout"));
        when(orderItems.findBestSellingProductIds(anyCollection(), eq(10L), any())).thenReturn(List.of(2L));
        when(products.findAllById(List.of(2L))).thenReturn(List.of(product(2, 1)));
        assertEquals(2L, service.similar(1L, 1).recommendations().getFirst().product().getId());
        verify(orderItems).findBestSellingProductIds(argThat(ids -> ids.contains(1L)), eq(10L), any());
    }
}
