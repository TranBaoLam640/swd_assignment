package com.freshmart.backend.dto.request.cart_management_module;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for UC13 - Update Cart Item Quantity. Quantity 0 is allowed
 * on purpose: per UC13's alternative flow, decreasing to 0 forwards to
 * UC17 - Remove Item from Cart instead of being rejected.
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateCartItemRequest {

    @NotNull
    @PositiveOrZero
    private Integer quantity;
}
