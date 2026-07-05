/**
 * JPA entities shared across multiple modules (Order, OrderItem, Payment,
 * Refund, User, Address, Shop, Shipper, Product, Waybill, OrderTracking,
 * Notification, etc.).
 *
 * <p>Kept flat (not split per module) by design: many entities here (e.g.
 * {@code Order}) are referenced by several modules (order, delivery,
 * payment, refund), so splitting them per module would duplicate table
 * ownership and cause circular package dependencies. Each entity's Javadoc
 * should note which module conceptually "owns" it.
 */
package com.freshmart.backend.data_access.entity;
