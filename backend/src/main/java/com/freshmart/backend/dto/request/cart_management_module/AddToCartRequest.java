package com.freshmart.backend.dto.request.cart_management_module;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request body for UC11 - Add Product to Cart. */
@Getter
@Setter
@NoArgsConstructor
public class AddToCartRequest {

    @NotNull
    private Long productId;

    @NotNull
    @Min(1)
    private Integer quantity;
}
