package com.freshmart.backend.exception.order_management_module;

import com.freshmart.backend.common.exception.BusinessException;

/**
 * Thrown by UC33 - Checkout (Step 2 / Step 6 alternative flow) when a
 * cart item's product no longer exists or has been deactivated since it
 * was added to the cart.
 */
public class ProductUnavailableException extends BusinessException {
    public ProductUnavailableException(Long productId) {
        super("Product " + productId + " is no longer available for purchase.");
    }
}
