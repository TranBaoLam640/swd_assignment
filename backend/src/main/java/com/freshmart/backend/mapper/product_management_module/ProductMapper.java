package com.freshmart.backend.mapper.product_management_module;

import org.springframework.stereotype.Component;

import com.freshmart.backend.data_access.entity.Product;
import com.freshmart.backend.dto.response.product_management_module.ProductResponse;

@Component
public class ProductMapper {

    /**
     * stockQuantity comes from the Inventory module (Product itself has no
     * stock column, see Product's own Javadoc) — callers look it up via
     * InventoryService and pass it in here rather than this mapper
     * reaching into another module's repository directly.
     */
    public ProductResponse toResponse(Product product, Integer stockQuantity) {
        return new ProductResponse(
                product.getId(),
                product.getShopId(),
                product.getCategoryId(),
                product.getProductName(),
                product.getDescription(),
                product.getPrice(),
                product.getImageUrl(),
                product.getIsActive(),
                stockQuantity
        );
    }
}
