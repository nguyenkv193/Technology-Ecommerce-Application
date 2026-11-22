package com.project.techstore.order.repository;

import com.project.techstore.order.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

    boolean existsByGatewayTransactionId(String gatewayTransactionId);

    Optional<PaymentTransaction> findByGatewayTransactionId(String gatewayTransactionId);
}
