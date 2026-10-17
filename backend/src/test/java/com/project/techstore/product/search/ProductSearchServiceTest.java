package com.project.techstore.product.search;

import com.project.techstore.product.dto.ProductFilterRequest;
import com.project.techstore.product.entity.ProductStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductSearchServiceTest {

    @Mock
    private ElasticsearchOperations operations;

    @InjectMocks
    private ProductSearchService searchService;

    @Test
    @SuppressWarnings("unchecked")
    void mapsSearchHitsIntoExistingCatalogPageResponse() {
        ProductSearchDocument document = ProductSearchDocument.builder()
                .id(21L)
                .productId(21L)
                .name("MacBook Pro M3")
                .slug("macbook-pro-m3")
                .status(ProductStatus.ACTIVE.name())
                .minPrice(new BigDecimal("39990000"))
                .maxPrice(new BigDecimal("39990000"))
                .totalStock(3)
                .variants(List.of())
                .build();
        SearchHit<ProductSearchDocument> hit = mock(SearchHit.class);
        when(hit.getContent()).thenReturn(document);
        SearchHits<ProductSearchDocument> hits = mock(SearchHits.class);
        when(hits.getSearchHits()).thenReturn(List.of(hit));
        when(hits.getTotalHits()).thenReturn(1L);
        when(operations.search(any(Query.class), eq(ProductSearchDocument.class))).thenReturn(hits);

        var result = searchService.search(ProductFilterRequest.builder()
                .keyword("MacBook")
                .page(0)
                .size(9)
                .sortBy("relevance")
                .build());

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(21L, result.getContent().get(0).getId());
        assertEquals("MacBook Pro M3", result.getContent().get(0).getName());
    }

    @Test
    void translatesElasticsearchConnectionFailureForPostgresFallback() {
        when(operations.search(any(Query.class), eq(ProductSearchDocument.class)))
                .thenThrow(new DataAccessResourceFailureException("Elasticsearch unavailable"));

        assertThrows(SearchUnavailableException.class, () -> searchService.search(
                ProductFilterRequest.builder()
                        .keyword("MacBook")
                        .build()
        ));
    }
}
