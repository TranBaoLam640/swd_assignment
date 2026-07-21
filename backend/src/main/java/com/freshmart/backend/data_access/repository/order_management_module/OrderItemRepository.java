package com.freshmart.backend.data_access.repository.order_management_module;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.freshmart.backend.data_access.entity.OrderItem;

/** Matches OrderItemRepository needs for UC16 (order detail line items). */
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);
}
