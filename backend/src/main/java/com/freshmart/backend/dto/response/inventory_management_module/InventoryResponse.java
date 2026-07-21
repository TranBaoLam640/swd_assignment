package com.freshmart.backend.dto.response.inventory_management_module;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Response for viewing/adjusting a product's stock quantity. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {
    private Long inventoryId;
    private Long productId;
    private Integer stockQuantity;
}
