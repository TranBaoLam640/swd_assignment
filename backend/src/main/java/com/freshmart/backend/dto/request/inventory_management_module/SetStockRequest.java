package com.freshmart.backend.dto.request.inventory_management_module;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Sets stock to an absolute value (matches Inventory.updateStock() in the
 * class diagram, and UC29 - Update Stock Quantity). Per UC29's AS1,
 * negative values are rejected.
 */
@Getter
@Setter
@NoArgsConstructor
public class SetStockRequest {

    @NotNull
    @PositiveOrZero
    private Integer quantity;
}
