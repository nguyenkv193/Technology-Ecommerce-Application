package com.project.techstore.user.entity;

import com.project.techstore.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entity sổ địa chỉ giao hàng của người dùng.
 * Một người dùng có thể có nhiều địa chỉ, trong đó có 1 địa chỉ mặc định.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "addresses")
public class Address extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "recipient_name", nullable = false, length = 255)
    private String recipientName;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "province", nullable = false, length = 100)
    private String province;

    @Column(name = "district", nullable = false, length = 100)
    private String district;

    @Column(name = "ward", nullable = false, length = 100)
    private String ward;

    @Column(name = "address_detail", nullable = false, length = 500)
    private String addressDetail;

    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private Boolean isDefault = false;
}
