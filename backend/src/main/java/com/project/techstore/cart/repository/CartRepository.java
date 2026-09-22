package com.project.techstore.cart.repository;

import com.project.techstore.cart.entity.Cart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    @EntityGraph(attributePaths = {"items", "items.variant", "items.variant.product", "items.variant.product.images"})
    Optional<Cart> findWithDetailsByUserId(Long userId);

    Optional<Cart> findByUserId(Long userId);
}
