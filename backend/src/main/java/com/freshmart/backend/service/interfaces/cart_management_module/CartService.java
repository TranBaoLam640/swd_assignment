package com.freshmart.backend.service.interfaces.cart_management_module;

import java.util.List;

import com.freshmart.backend.dto.request.cart_management_module.AddToCartRequest;
import com.freshmart.backend.dto.request.cart_management_module.UpdateCartItemRequest;
import com.freshmart.backend.dto.response.cart_management_module.CartItemResponse;

/** Implements UC11-UC14 (Add to Cart, View Cart, Update Quantity, Remove Item). */
public interface CartService {

    /** Adds quantity to an existing row for the same product, or creates a new one. */
    CartItemResponse addToCart(Long userId, AddToCartRequest request);

    List<CartItemResponse> viewCart(Long userId);

    /**
     * Per UC13's alternative flow: decreasing quantity to 0 forwards to
     * Remove Item instead of updating. Returns {@code null} when the item
     * was removed this way.
     */
    CartItemResponse updateQuantity(Long userId, Long cartItemId, UpdateCartItemRequest request);

    boolean removeItem(Long userId, Long cartItemId);
}
