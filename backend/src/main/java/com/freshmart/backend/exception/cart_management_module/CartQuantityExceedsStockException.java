package com.freshmart.backend.exception.cart_management_module;

import com.freshmart.backend.common.exception.BusinessException;

/**
 * Thrown by CartServiceImpl when adding/updating a cart line would put its
 * quantity above the product's current Inventory.stockQuantity. Mirrors the
 * same rule enforced client-side (ProductListPage/ProductDetailPage), so a
 * request that bypasses the UI (or a stale client) is still rejected.
 */
public class CartQuantityExceedsStockException extends BusinessException {
    public CartQuantityExceedsStockException(int availableStock) {
        super(400, "Số lượng vượt quá số lượng hiện có trong kho (còn lại " + availableStock + " sản phẩm).");
    }
}
