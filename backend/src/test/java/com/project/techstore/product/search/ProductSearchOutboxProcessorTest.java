package com.project.techstore.product.search;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductSearchOutboxProcessorTest {

    @Mock
    private ProductSearchOutboxRepository outboxRepository;

    @Mock
    private ProductSearchIndexService indexService;

    @InjectMocks
    private ProductSearchOutboxProcessor processor;

    @Test
    void processesUpsertAndMarksOutboxEventComplete() {
        ProductSearchOutboxEvent event =
                ProductSearchOutboxEvent.pending(45L, ProductSearchEventType.UPSERT);
        event.setId(7L);
        when(outboxRepository
                .findTop50ByProcessedAtIsNullAndNextAttemptAtLessThanEqualOrderByIdAsc(any(Instant.class)))
                .thenReturn(List.of(event));

        processor.processPendingEvents();

        verify(indexService).upsertProduct(45L);
        verify(outboxRepository).save(event);
        assertNotNull(event.getProcessedAt());
        assertEquals(0, event.getAttempts());
    }

    @Test
    void keepsFailedEventPendingAndSchedulesRetry() {
        ProductSearchOutboxEvent event =
                ProductSearchOutboxEvent.pending(45L, ProductSearchEventType.UPSERT);
        event.setId(8L);
        when(outboxRepository
                .findTop50ByProcessedAtIsNullAndNextAttemptAtLessThanEqualOrderByIdAsc(any(Instant.class)))
                .thenReturn(List.of(event));
        doThrow(new IllegalStateException("Elasticsearch unavailable"))
                .when(indexService).upsertProduct(45L);

        processor.processPendingEvents();

        verify(outboxRepository).save(event);
        assertEquals(1, event.getAttempts());
        assertEquals("Elasticsearch unavailable", event.getLastError());
        assertNotNull(event.getNextAttemptAt());
        assertEquals(null, event.getProcessedAt());
    }
}
