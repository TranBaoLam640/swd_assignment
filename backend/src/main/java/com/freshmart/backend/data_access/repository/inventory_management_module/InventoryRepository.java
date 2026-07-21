package com.freshmart.backend.data_access.repository.inventory_management_module;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.freshmart.backend.data_access.entity.Inventory;

/** Matches Inventory repository needs (strict one-to-one with Product). */
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductId(Long productId);

    /** Batch lookup — used to build a productId -> stockQuantity map without N+1 queries. */
    List<Inventory> findByProductIdIn(List<Long> productIds);
}
