package com.freshmart.backend.dto.response.product_management_module;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductReviewResponse {
    private Long reviewId;
    private Long orderId;
    private Long productId;
    private String reviewerName;
    private Integer rating;
    private String comment;
    private String imageUrl;
    private String videoUrl;
    private Instant createdAt;
}
