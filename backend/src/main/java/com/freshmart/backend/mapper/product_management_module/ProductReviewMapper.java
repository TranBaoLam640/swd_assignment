package com.freshmart.backend.mapper.product_management_module;

import org.springframework.stereotype.Component;

import com.freshmart.backend.data_access.entity.ProductReview;
import com.freshmart.backend.dto.response.product_management_module.ProductReviewResponse;

@Component
public class ProductReviewMapper {

    public ProductReviewResponse toResponse(ProductReview review, String reviewerName) {
        return new ProductReviewResponse(
                review.getId(),
                review.getOrderId(),
                review.getProductId(),
                reviewerName,
                review.getRating(),
                review.getComment(),
                review.getImageUrl(),
                review.getVideoUrl(),
                review.getCreatedAt()
        );
    }
}
