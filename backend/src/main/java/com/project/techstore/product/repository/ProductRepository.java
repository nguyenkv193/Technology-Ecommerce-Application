package com.project.techstore.product.repository;

import com.project.techstore.product.entity.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @EntityGraph(attributePaths = {"category", "brand"})
    Optional<Product> findWithDetailsBySlug(String slug);

    @EntityGraph(attributePaths = {"category", "brand"})
    Optional<Product> findWithDetailsById(Long id);

    Optional<Product> findBySlug(String slug);

    boolean existsBySlug(String slug);
}
