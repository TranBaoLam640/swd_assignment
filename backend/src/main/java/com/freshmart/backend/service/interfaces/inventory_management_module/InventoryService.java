package com.freshmart.backend.service.interfaces.inventory_management_module;

import java.util.List;
import java.util.Map;

import com.freshmart.backend.dto.response.inventory_management_module.InventoryResponse;

/**
 * Matches the Inventory class diagram (getInventory/increaseStock/
 * decreaseStock/updateStock). Implements FE-08 (Inventory Management),
 * simplified to the SDS DB schema's one-row-per-product model rather than
 * the richer per-batch tracking described in SRS UC26-32 (see
 * InventoryServiceImpl's Javadoc for the discrepancy).
 */
public interface InventoryService {

    InventoryResponse getByProduct(Long productId);

    /** Creates the initial (stockQuantity = 0) row for a newly created product. */
    InventoryResponse createForProduct(Long productId);

    InventoryResponse increaseStock(Long productId, int quantity);

    InventoryResponse decreaseStock(Long productId, int quantity);

    /** Sets stock to an absolute value (UC29 - Update Stock Quantity). */
    InventoryResponse setStock(Long productId, int quantity);

    /**
     * Batch stock lookup keyed by productId — used by ProductServiceImpl to
     * populate ProductResponse.stockQuantity for browse/search results
     * without one query per product. A productId with no Inventory row
     * simply isn't a key in the returned map (shouldn't normally happen,
     * since every product gets one at creation — callers should default
     * missing entries to 0).
     */
    Map<Long, Integer> getStockMap(List<Long> productIds);
}
