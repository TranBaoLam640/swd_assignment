package com.freshmart.backend.service.impl.inventory_management_module;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.freshmart.backend.common.exception.ResourceNotFoundException;
import com.freshmart.backend.data_access.entity.Inventory;
import com.freshmart.backend.data_access.repository.inventory_management_module.InventoryRepository;
import com.freshmart.backend.dto.response.inventory_management_module.InventoryResponse;
import com.freshmart.backend.exception.inventory_management_module.InsufficientStockException;
import com.freshmart.backend.mapper.inventory_management_module.InventoryMapper;
import com.freshmart.backend.service.interfaces.inventory_management_module.InventoryService;

/**
 * Implements FE-08 (Inventory Management) matching the SDS DB schema
 * (2.2.8 inventory: one row per product, stockQuantity + last_updated)
 * and the Inventory class diagram's own methods
 * (getInventory/increaseStock/decreaseStock/updateStock).
 *
 * <p><b>Known SRS/SDS mismatch, found while implementing this:</b> SRS
 * section 3.4 "Batch Management" (UC26-32) describes a considerably
 * richer model — each imported <i>batch</i> has its own current stock,
 * selling price, expiration date, supplier, and a cancellable status, plus
 * separate Stock Change History / Price Change History logs. None of
 * that exists in the SDS database design: {@code batch_receipt} (2.2.9)
 * only has quantity_added/import_price/received_at (no expiration date,
 * supplier, status, or per-batch current-stock column), and
 * {@code inventory} (2.2.8) has no per-batch breakdown at all — just one
 * total per product. This class implements the simpler SDS-schema model
 * (what was asked for: create product, then add/subtract quantity on its
 * single inventory row). If you actually want per-batch tracking with
 * expiration/supplier/history as SRS UC26-32 describe, the database design
 * needs new columns/tables first — worth reconciling with your team before
 * building further on top of this.
 */
@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;

    public InventoryServiceImpl(InventoryRepository inventoryRepository, InventoryMapper inventoryMapper) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryMapper = inventoryMapper;
    }

    @Override
    public InventoryResponse getByProduct(Long productId) {
        return inventoryMapper.toResponse(findByProduct(productId));
    }

    @Override
    @Transactional
    public InventoryResponse createForProduct(Long productId) {
        Inventory inventory = new Inventory();
        inventory.setProductId(productId);
        inventory.setStockQuantity(0);
        inventoryRepository.save(inventory);
        return inventoryMapper.toResponse(inventory);
    }

    @Override
    @Transactional
    public InventoryResponse increaseStock(Long productId, int quantity) {
        Inventory inventory = findByProduct(productId);
        inventory.setStockQuantity(inventory.getStockQuantity() + quantity);
        inventoryRepository.save(inventory);
        return inventoryMapper.toResponse(inventory);
    }

    @Override
    @Transactional
    public InventoryResponse decreaseStock(Long productId, int quantity) {
        Inventory inventory = findByProduct(productId);
        int newStock = inventory.getStockQuantity() - quantity;
        if (newStock < 0) {
            throw new InsufficientStockException(productId, inventory.getStockQuantity(), quantity);
        }
        inventory.setStockQuantity(newStock);
        inventoryRepository.save(inventory);
        return inventoryMapper.toResponse(inventory);
    }

    @Override
    @Transactional
    public InventoryResponse setStock(Long productId, int quantity) {
        Inventory inventory = findByProduct(productId);
        inventory.setStockQuantity(quantity);
        inventoryRepository.save(inventory);
        return inventoryMapper.toResponse(inventory);
    }

    @Override
    public Map<Long, Integer> getStockMap(List<Long> productIds) {
        return inventoryRepository.findByProductIdIn(productIds).stream()
                .collect(Collectors.toMap(Inventory::getProductId, Inventory::getStockQuantity));
    }

    private Inventory findByProduct(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> ResourceNotFoundException.of("Inventory for product", productId));
    }
}
