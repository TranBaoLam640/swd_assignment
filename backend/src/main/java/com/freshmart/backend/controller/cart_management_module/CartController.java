package com.freshmart.backend.controller.cart_management_module;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freshmart.backend.common.dto.ApiResponse;
import com.freshmart.backend.dto.request.cart_management_module.AddToCartRequest;
import com.freshmart.backend.dto.request.cart_management_module.UpdateCartItemRequest;
import com.freshmart.backend.dto.response.cart_management_module.CartItemResponse;
import com.freshmart.backend.service.interfaces.cart_management_module.CartService;

import jakarta.validation.Valid;

/** Implements UC11-UC14. Customer identity always comes from the JWT principal. */
@RestController
@RequestMapping("/api/v1/customer/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping
    public ApiResponse<CartItemResponse> addToCart(@AuthenticationPrincipal Long userId,
                                                    @Valid @RequestBody AddToCartRequest request) {
        return ApiResponse.success(201, "Added to cart", cartService.addToCart(userId, request));
    }

    @GetMapping
    public ApiResponse<List<CartItemResponse>> viewCart(@AuthenticationPrincipal Long userId) {
        return ApiResponse.success(cartService.viewCart(userId));
    }

    @PutMapping("/{cartItemId}")
    public ApiResponse<CartItemResponse> updateQuantity(@AuthenticationPrincipal Long userId,
                                                         @PathVariable Long cartItemId,
                                                         @Valid @RequestBody UpdateCartItemRequest request) {
        CartItemResponse response = cartService.updateQuantity(userId, cartItemId, request);
        String message = response == null ? "Item removed (quantity reached 0)" : "Quantity updated";
        return ApiResponse.success(200, message, response);
    }

    @DeleteMapping("/{cartItemId}")
    public ApiResponse<Void> removeItem(@AuthenticationPrincipal Long userId,
                                         @PathVariable Long cartItemId) {
        cartService.removeItem(userId, cartItemId);
        return ApiResponse.success(200, "Item removed", null);
    }
}
