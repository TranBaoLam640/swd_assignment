package com.freshmart.backend.data_access.repository.product_management_module;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.freshmart.backend.data_access.entity.ProductReview;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
    boolean existsByOrderIdAndProductId(Long orderId, Long productId);

    Optional<ProductReview> findByOrderIdAndProductId(Long orderId, Long productId);

    List<ProductReview> findByOrderId(Long orderId);

    List<ProductReview> findByProductIdOrderByCreatedAtDesc(Long productId);

    @Query("SELECT r.productId, AVG(r.rating), COUNT(r) FROM ProductReview r WHERE r.productId IN :productIds GROUP BY r.productId")
    List<Object[]> summarizeByProductIds(@Param("productIds") List<Long> productIds);
}
