package com.project.techstore.product.repository;

import com.project.techstore.product.entity.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.project.techstore.product.entity.ProductStatus;

import java.util.List;
import java.util.Collection;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @EntityGraph(attributePaths = {"category", "brand"})
    Page<Product> findByStatus(ProductStatus status, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.status = com.project.techstore.product.entity.ProductStatus.ACTIVE " +
            "AND p.id NOT IN :excludedIds AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
            "AND EXISTS (SELECT v.id FROM ProductVariant v WHERE v.product = p AND v.status = 'ACTIVE' AND v.stock > 0) " +
            "ORDER BY p.createdAt DESC, p.id DESC")
    List<Product> findAvailableForRecommendations(@Param("excludedIds") Collection<Long> excludedIds,
                                                @Param("categoryId") Long categoryId, Pageable pageable);

    @EntityGraph(attributePaths = {"category", "brand"})
    Optional<Product> findWithDetailsBySlug(String slug);

    @EntityGraph(attributePaths = {"category", "brand"})
    Optional<Product> findWithDetailsById(Long id);

    Optional<Product> findBySlug(String slug);

    boolean existsBySlug(String slug);
}
