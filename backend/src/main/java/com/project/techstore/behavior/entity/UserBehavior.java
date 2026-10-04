package com.project.techstore.behavior.entity;

import com.project.techstore.product.entity.Product;
import com.project.techstore.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

/**
 * Entity lưu vết sự kiện hành vi người dùng (Clickstream / Event Log).
 * Nguồn dữ liệu cốt lõi để tính toán ma trận tương tác User-Item cho mô hình AI Gợi ý.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_behaviors")
@EntityListeners(AuditingEntityListener.class)
public class UserBehavior {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "session_id", length = 100)
    private String sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 50)
    private BehaviorType actionType;

    @Column(name = "action_value", length = 500)
    private String actionValue;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
