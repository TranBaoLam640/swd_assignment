package com.freshmart.backend.mapper.inventory_management_module;

import org.springframework.stereotype.Component;

import com.freshmart.backend.data_access.entity.Inventory;
import com.freshmart.backend.dto.response.inventory_management_module.InventoryResponse;

@Component
public class InventoryMapper {

    public InventoryResponse toResponse(Inventory inventory) {
        return new InventoryResponse(
                inventory.getId(),
                inventory.getProductId(),
                inventory.getStockQuantity()
        );
    }
}
