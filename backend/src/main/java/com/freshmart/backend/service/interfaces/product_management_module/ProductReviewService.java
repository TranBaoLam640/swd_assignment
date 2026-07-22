package com.freshmart.backend.service.interfaces.product_management_module;

import java.util.List;

import com.freshmart.backend.dto.request.product_management_module.CreateProductReviewRequest;
import com.freshmart.backend.dto.response.product_management_module.ProductReviewResponse;

public interface ProductReviewService {
    List<ProductReviewResponse> listProductReviews(Long productId);

    List<ProductReviewResponse> listMyOrderReviews(Long customerId, Long orderId);

    ProductReviewResponse createReview(Long customerId, CreateProductReviewRequest request);
}
