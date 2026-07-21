package com.freshmart.backend.dto.request.inventory_management_module;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Amount to add or remove from current stock (used by both the
 * increase and decrease endpoints — matches Inventory.increaseStock()/
 * decreaseStock() in the class diagram). Always a positive delta; the
 * endpoint decides the direction.
 */
@Getter
@Setter
@NoArgsConstructor
public class AdjustStockRequest {

    @NotNull
    @Min(1)
    private Integer quantity;
}
