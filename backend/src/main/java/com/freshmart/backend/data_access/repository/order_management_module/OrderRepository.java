package com.freshmart.backend.data_access.repository.order_management_module;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.freshmart.backend.data_access.entity.Order;
import com.freshmart.backend.enums.order_management_module.OrderStatus;

/** Matches OrderRepository needs for UC15 (order history) + UC33 (checkout). */
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    /** Used by OrderExpiryScheduler to find PENDING_PAYMENT orders that have gone stale. */
    List<Order> findByStatusAndCreatedAtBefore(OrderStatus status, Instant cutoff);
}
