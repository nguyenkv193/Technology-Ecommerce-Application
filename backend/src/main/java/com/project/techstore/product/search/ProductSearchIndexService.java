package com.project.techstore.product.search;

import com.project.techstore.product.entity.Product;
import com.project.techstore.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSearchIndexService {

    private static final int REINDEX_BATCH_SIZE = 200;

    private final ElasticsearchOperations operations;
    private final ProductRepository productRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional(readOnly = true)
    public synchronized void initializeIndex() {
        try {
            ensureIndex();
        } catch (RuntimeException exception) {
            // Keep the backend available; search falls back to PostgreSQL until Elasticsearch recovers.
            log.warn("Could not initialize Elasticsearch product index; PostgreSQL search fallback remains enabled: {}",
                    exception.getMessage());
        }
    }

    @Scheduled(
            fixedDelayString = "${techstore.search.index-check-interval-ms:10000}",
            initialDelayString = "${techstore.search.index-check-interval-ms:10000}"
    )
    @Transactional(readOnly = true)
    public synchronized void ensureIndexAvailable() {
        try {
            ensureIndex();
        } catch (RuntimeException exception) {
            log.debug("Elasticsearch product index is not ready yet: {}", exception.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public synchronized void upsertProduct(Long productId) {
        ensureIndex();
        Product product = productRepository.findWithDetailsById(productId).orElse(null);
        if (product == null) {
            deleteProduct(productId);
            return;
        }
        operations.save(ProductSearchDocument.from(product));
    }

    public synchronized void deleteProduct(Long productId) {
        ensureIndex();
        operations.delete(productId.toString(), ProductSearchDocument.class);
    }

    @Transactional(readOnly = true)
    public synchronized long reindexAllProducts() {
        IndexOperations index = operations.indexOps(ProductSearchDocument.class);
        if (index.exists()) {
            index.delete();
        }
        index.createWithMapping();
        try {
            long indexed = indexCurrentCatalog();
            log.info("Rebuilt Elasticsearch product index with {} products", indexed);
            return indexed;
        } catch (RuntimeException exception) {
            index.delete();
            throw exception;
        }
    }

    private long indexCurrentCatalog() {
        long indexed = 0;
        int pageNumber = 0;
        Page<Product> page;
        do {
            page = productRepository.findAll(PageRequest.of(
                    pageNumber,
                    REINDEX_BATCH_SIZE,
                    Sort.by(Sort.Direction.ASC, "id")
            ));
            List<ProductSearchDocument> documents = page.getContent().stream()
                    .map(ProductSearchDocument::from)
                    .toList();
            if (!documents.isEmpty()) {
                operations.save(documents);
                indexed += documents.size();
            }
            pageNumber++;
        } while (page.hasNext());
        return indexed;
    }

    private boolean ensureIndex() {
        IndexOperations index = operations.indexOps(ProductSearchDocument.class);
        if (!index.exists()) {
            index.createWithMapping();
            try {
                long indexed = indexCurrentCatalog();
                log.info("Created product search index and indexed {} products", indexed);
            } catch (RuntimeException exception) {
                index.delete();
                throw exception;
            }
            return true;
        }
        return false;
    }
}
