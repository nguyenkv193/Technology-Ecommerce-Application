package com.project.techstore.banner.entity;

import com.project.techstore.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;

/**
 * Entity Banner quảng cáo / Hero Slider / Khuyến mãi trong hệ thống TechStore.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "banners")
public class Banner extends BaseEntity {

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "mobile_image_url", length = 500)
    private String mobileImageUrl;

    @Column(name = "link_url", nullable = false, length = 500)
    private String linkUrl;

    @Column(name = "target_type", nullable = false, length = 20)
    @Builder.Default
    private String targetType = "_self";

    @Column(name = "position", nullable = false, length = 50)
    @Builder.Default
    private String position = "HERO_SLIDER";

    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;

    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @Column(name = "click_count", nullable = false)
    @Builder.Default
    private Long clickCount = 0L;
}
