package com.project.techstore.order.repository;

import com.project.techstore.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT p.id FROM OrderItem i JOIN i.variant v JOIN v.product p JOIN i.order o " +
            "WHERE o.orderStatus = com.project.techstore.order.entity.OrderStatus.DELIVERED " +
            "AND o.paymentStatus = com.project.techstore.order.entity.PaymentStatus.PAID " +
            "AND p.status = com.project.techstore.product.entity.ProductStatus.ACTIVE " +
            "AND p.id NOT IN :excludedIds AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
            "AND EXISTS (SELECT available.id FROM ProductVariant available " +
            "WHERE available.product = p AND available.status = 'ACTIVE' AND available.stock > 0) " +
            "GROUP BY p.id ORDER BY SUM(i.quantity) DESC, p.id ASC")
    List<Long> findBestSellingProductIds(@Param("excludedIds") Collection<Long> excludedIds,
                                        @Param("categoryId") Long categoryId, Pageable pageable);

    List<OrderItem> findByOrderId(Long orderId);
}
