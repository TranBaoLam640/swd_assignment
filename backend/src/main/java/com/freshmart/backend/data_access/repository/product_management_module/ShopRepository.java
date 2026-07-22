package com.freshmart.backend.data_access.repository.product_management_module;

import org.springframework.data.jpa.repository.JpaRepository;

import com.freshmart.backend.data_access.entity.Shop;

/** Repository for manager shop lookup data. */
public interface ShopRepository extends JpaRepository<Shop, Long> {
}
