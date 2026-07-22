package com.freshmart.backend.dto.response.product_management_module;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Response for customer product browsing (UC9/UC10) and manager product CRUD. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long productId;
    private Long shopId;
    private String shopName;
    private Long categoryId;
    private String categoryName;
    private String productName;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private Boolean isActive;
    private Double averageRating;
    private Long reviewCount;

    /**
     * Current Inventory.stockQuantity for this product — added so the
     * storefront/cart UI can validate "don't add more than what the shop
     * actually has" client-side before calling POST /cart, instead of only
     * finding out after a failed request.
     */
    private Integer stockQuantity;
}
