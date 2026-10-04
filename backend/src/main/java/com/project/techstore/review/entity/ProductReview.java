package com.project.techstore.review.entity;

import com.project.techstore.common.entity.BaseEntity;
import com.project.techstore.product.entity.Product;
import com.project.techstore.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_reviews", uniqueConstraints = {
        @UniqueConstraint(name = "uq_review_user_product", columnNames = {"user_id", "product_id"})
})
public class ProductReview extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;
}
