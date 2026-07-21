package com.freshmart.backend.mapper.cart_management_module;

import org.springframework.stereotype.Component;

import com.freshmart.backend.data_access.entity.CartItem;
import com.freshmart.backend.dto.response.cart_management_module.CartItemResponse;

@Component
public class CartItemMapper {

    public CartItemResponse toResponse(CartItem cartItem) {
        return new CartItemResponse(
                cartItem.getId(),
                cartItem.getProductId(),
                cartItem.getQuantity(),
                cartItem.getCreatedAt()
        );
    }
}
