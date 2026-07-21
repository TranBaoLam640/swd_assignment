package com.freshmart.backend.data_access.repository.product_management_module;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.freshmart.backend.data_access.entity.Product;

/** Matches ProductRepository in the class diagram. */
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByIsActiveTrue();

    List<Product> findByShopIdAndIsActiveTrue(Long shopId);

    /**
     * UC09 - Browse & Search Product: keyword and categoryId are both
     * optional (pass null for either to mean "no filter on this
     * criterion"). Only ever returns active products — BR-20 says inactive
     * products aren't purchasable, and per UC09/BR-01 they shouldn't show
     * up in the public browse/search results either.
     */
    @Query("SELECT p FROM Product p WHERE p.isActive = true "
            + "AND (:keyword IS NULL OR :keyword = '' OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))) "
            + "AND (:categoryId IS NULL OR p.categoryId = :categoryId)")
    List<Product> searchActiveProducts(@Param("keyword") String keyword, @Param("categoryId") Long categoryId);
}
