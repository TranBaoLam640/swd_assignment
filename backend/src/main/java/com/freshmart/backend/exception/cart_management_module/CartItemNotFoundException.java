package com.freshmart.backend.exception.cart_management_module;

import com.freshmart.backend.common.exception.ResourceNotFoundException;

/**
 * Thrown when a cart item id doesn't exist, or exists but belongs to a
 * different customer (an ownership mismatch is deliberately reported the
 * same way as "not found", to avoid leaking another user's cart contents).
 */
public class CartItemNotFoundException extends ResourceNotFoundException {
    public CartItemNotFoundException(Long cartItemId) {
        super("Cart item not found with id: " + cartItemId);
    }
}
