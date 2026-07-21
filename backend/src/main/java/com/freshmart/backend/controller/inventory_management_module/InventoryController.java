package com.freshmart.backend.controller.inventory_management_module;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freshmart.backend.common.dto.ApiResponse;
import com.freshmart.backend.dto.request.inventory_management_module.AdjustStockRequest;
import com.freshmart.backend.dto.request.inventory_management_module.SetStockRequest;
import com.freshmart.backend.dto.response.inventory_management_module.InventoryResponse;
import com.freshmart.backend.service.interfaces.inventory_management_module.InventoryService;

import jakarta.validation.Valid;

/**
 * Manager-only endpoints under /api/v1/manager/** (already restricted to
 * MANAGER by the existing SecurityConfig rule — no change needed there).
 */
@RestController
@RequestMapping("/api/v1/manager/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/{productId}")
    public ApiResponse<InventoryResponse> getStock(@PathVariable Long productId) {
        return ApiResponse.success(inventoryService.getByProduct(productId));
    }

    @PostMapping("/{productId}/increase")
    public ApiResponse<InventoryResponse> increaseStock(@PathVariable Long productId,
                                                         @Valid @RequestBody AdjustStockRequest request) {
        InventoryResponse response = inventoryService.increaseStock(productId, request.getQuantity());
        return ApiResponse.success(200, "Stock increased", response);
    }

    @PostMapping("/{productId}/decrease")
    public ApiResponse<InventoryResponse> decreaseStock(@PathVariable Long productId,
                                                         @Valid @RequestBody AdjustStockRequest request) {
        InventoryResponse response = inventoryService.decreaseStock(productId, request.getQuantity());
        return ApiResponse.success(200, "Stock decreased", response);
    }

    @PutMapping("/{productId}")
    public ApiResponse<InventoryResponse> setStock(@PathVariable Long productId,
                                                    @Valid @RequestBody SetStockRequest request) {
        InventoryResponse response = inventoryService.setStock(productId, request.getQuantity());
        return ApiResponse.success(200, "Stock updated", response);
    }
}
