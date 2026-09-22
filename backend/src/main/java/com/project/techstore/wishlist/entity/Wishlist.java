package com.project.techstore.wishlist.entity;

import com.project.techstore.common.entity.BaseEntity;
import com.project.techstore.product.entity.Product;
import com.project.techstore.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entity lưu danh sách sản phẩm yêu thích (Wishlist).
 * Đóng vai trò cực kỳ quan trọng làm dữ liệu huấn luyện cho hệ thống AI Recommendation.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wishlists", uniqueConstraints = {
        @UniqueConstraint(name = "uq_wishlist_user_product", columnNames = {"user_id", "product_id"})
})
public class Wishlist extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
