package com.freshmart.backend.data_access.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Customer review for a purchased product in a specific order. */
@Entity
@Table(name = "product_review",
        uniqueConstraints = @UniqueConstraint(name = "uk_review_order_product", columnNames = {"order_id", "product_id"}))
@Getter
@Setter
@NoArgsConstructor
public class ProductReview extends BaseEntity {

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "video_url", length = 255)
    private String videoUrl;
}
