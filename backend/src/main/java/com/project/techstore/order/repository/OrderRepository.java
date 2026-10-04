package com.project.techstore.order.repository;

import com.project.techstore.order.entity.Order;
import com.project.techstore.order.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"items", "user"})
    Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"items", "user"})
    Optional<Order> findWithDetailsById(Long id);

    @EntityGraph(attributePaths = {"items", "user"})
    Optional<Order> findWithDetailsByOrderCode(String orderCode);

    Page<Order> findByOrderStatusOrderByCreatedAtDesc(OrderStatus orderStatus, Pageable pageable);

    boolean existsByOrderCode(String orderCode);
}
