package com.project.techstore.product.repository;

import com.project.techstore.product.entity.ProductStorefrontContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductStorefrontContentRepository extends JpaRepository<ProductStorefrontContent, Long> {
    Optional<ProductStorefrontContent> findByProductId(Long productId);
}
