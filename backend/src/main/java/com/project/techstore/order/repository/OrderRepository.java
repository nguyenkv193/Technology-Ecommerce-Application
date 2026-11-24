package com.project.techstore.order.repository;

import com.project.techstore.order.entity.Order;
import com.project.techstore.order.entity.OrderStatus;
import com.project.techstore.order.entity.PaymentMethod;
import com.project.techstore.order.entity.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"items", "user"})
    Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"items", "user"})
    Page<Order> findByUserIdAndOrderStatusOrderByCreatedAtDesc(Long userId, OrderStatus orderStatus, Pageable pageable);

    @EntityGraph(attributePaths = {"items", "user"})
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId " +
            "AND (:status IS NULL OR o.orderStatus = :status) " +
            "AND (LOWER(o.orderCode) LIKE :keywordPattern " +
            "OR EXISTS (SELECT 1 FROM OrderItem i WHERE i.order = o AND LOWER(i.productName) LIKE :keywordPattern)) " +
            "ORDER BY o.createdAt DESC")
    Page<Order> searchUserOrders(
            @Param("userId") Long userId,
            @Param("status") OrderStatus status,
            @Param("keywordPattern") String keywordPattern,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"items", "user"})
    Optional<Order> findWithDetailsById(Long id);

    @EntityGraph(attributePaths = {"items", "user"})
    Optional<Order> findWithDetailsByOrderCode(String orderCode);

    @EntityGraph(attributePaths = {"items", "user"})
    @Query("SELECT o FROM Order o WHERE UPPER(REPLACE(o.orderCode, '-', '')) = UPPER(REPLACE(:candidateCode, '-', ''))")
    Optional<Order> findWithDetailsByNormalizedOrderCode(@Param("candidateCode") String candidateCode);

    @EntityGraph(attributePaths = {"items", "items.variant", "user"})
    List<Order> findByOrderStatusAndPaymentStatusAndPaymentMethodInAndPaymentExpiresAtBefore(
            OrderStatus orderStatus,
            PaymentStatus paymentStatus,
            Collection<PaymentMethod> paymentMethods,
            Instant cutoff
    );

    @EntityGraph(attributePaths = {"items", "user"})
    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @EntityGraph(attributePaths = {"items", "user"})
    Page<Order> findByOrderStatusOrderByCreatedAtDesc(OrderStatus orderStatus, Pageable pageable);

    boolean existsByOrderCode(String orderCode);
}
