package com.project.techstore.product.entity;

import com.project.techstore.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entity lưu các thuộc tính thông số kỹ thuật (CPU, RAM, GPU, Màn hình, Dung lượng pin,...).
 * Phục vụ lọc động và tạo Vector Embedding / TF-IDF cho hệ thống gợi ý AI (Content-Based Cosine Similarity).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_attributes")
public class ProductAttribute extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "value", nullable = false, length = 255)
    private String value;
}
