package com.freshmart.backend.controller.product_management_module;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.freshmart.backend.common.dto.ApiResponse;
import com.freshmart.backend.dto.request.product_management_module.CreateProductReviewRequest;
import com.freshmart.backend.dto.response.product_management_module.ProductReviewResponse;
import com.freshmart.backend.service.interfaces.product_management_module.ProductReviewService;

import jakarta.validation.Valid;

@RestController
public class ProductReviewController {

    private final ProductReviewService reviewService;

    public ProductReviewController(ProductReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/api/v1/products/{productId}/reviews")
    public ApiResponse<List<ProductReviewResponse>> listProductReviews(@PathVariable Long productId) {
        return ApiResponse.success(reviewService.listProductReviews(productId));
    }

    @GetMapping("/api/v1/customer/reviews")
    public ApiResponse<List<ProductReviewResponse>> listMyOrderReviews(
            @AuthenticationPrincipal Long userId,
            @RequestParam Long orderId) {
        return ApiResponse.success(reviewService.listMyOrderReviews(userId, orderId));
    }

    @PostMapping("/api/v1/customer/reviews")
    public ApiResponse<ProductReviewResponse> createReview(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CreateProductReviewRequest request) {
        return ApiResponse.success(201, "Review submitted", reviewService.createReview(userId, request));
    }
}
