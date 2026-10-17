package com.project.techstore.product.search;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ProductSearchOutboxRepository extends JpaRepository<ProductSearchOutboxEvent, Long> {

    List<ProductSearchOutboxEvent> findTop50ByProcessedAtIsNullAndNextAttemptAtLessThanEqualOrderByIdAsc(
            Instant now
    );
}
