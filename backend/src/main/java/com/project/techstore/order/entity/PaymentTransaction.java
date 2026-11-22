package com.project.techstore.order.entity;

import com.project.techstore.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payment_transactions")
public class PaymentTransaction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "gateway", nullable = false, length = 30)
    @Builder.Default
    private String gateway = "SEPAY";

    @Column(name = "gateway_transaction_id", nullable = false, unique = true, length = 100)
    private String gatewayTransactionId;

    @Column(name = "transfer_type", length = 20)
    private String transferType;

    @Column(name = "transfer_amount", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal transferAmount = BigDecimal.ZERO;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "account_number", length = 50)
    private String accountNumber;

    @Column(name = "reference_code", length = 100)
    private String referenceCode;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "raw_payload", columnDefinition = "TEXT")
    private String rawPayload;
}
