package com.project.techstore.product.search;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductSearchOutboxProcessor {

    private final ProductSearchOutboxRepository outboxRepository;
    private final ProductSearchIndexService indexService;

    @Scheduled(fixedDelayString = "${techstore.search.outbox.poll-interval-ms:2000}")
    public void processPendingEvents() {
        List<ProductSearchOutboxEvent> pendingEvents =
                outboxRepository.findTop50ByProcessedAtIsNullAndNextAttemptAtLessThanEqualOrderByIdAsc(
                        Instant.now()
                );

        for (ProductSearchOutboxEvent event : pendingEvents) {
            try {
                if (event.getEventType() == ProductSearchEventType.DELETE) {
                    indexService.deleteProduct(event.getProductId());
                } else {
                    indexService.upsertProduct(event.getProductId());
                }
                event.setProcessedAt(Instant.now());
                event.setLastError(null);
            } catch (RuntimeException exception) {
                int attempts = event.getAttempts() + 1;
                event.setAttempts(attempts);
                long retryDelaySeconds = Math.min(300, 1L << Math.min(attempts, 8));
                event.setNextAttemptAt(Instant.now().plusSeconds(retryDelaySeconds));
                event.setLastError(truncate(exception.getMessage(), 2000));
                log.warn("Product search indexing event {} failed (attempt {}); retrying in {}s: {}",
                        event.getId(), attempts, retryDelaySeconds, exception.getMessage());
            }
            outboxRepository.save(event);
        }
    }

    private String truncate(String message, int maxLength) {
        if (message == null || message.length() <= maxLength) {
            return message;
        }
        return message.substring(0, maxLength);
    }
}
