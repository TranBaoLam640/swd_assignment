package com.freshmart.backend.dto.response.cart_management_module;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response for a single cart line item. Deliberately has no price/subtotal
 * field yet — that requires the Product entity, which doesn't exist. Add
 * it once Product exists, per UC12's requirement to show latest price and
 * calculated subtotal.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
    private Long cartItemId;
    private Long productId;
    private Integer quantity;
    private Instant addedAt;
}
