package com.freshmart.backend.enums.order_management_module;

/**
 * Parent-Order lifecycle status. Matches the states in SDS 3.9 "Order
 * state machine diagram" (Create, Fruit held, Pending payment, Confirmed,
 * Cancelled, expired) — governed exclusively by
 * {@link com.freshmart.backend.data_access.entity.OrderStateMachine}.
 *
 * <p>Note: these values differ from the order_status example values
 * documented on the order table in SDS 2.2.11 ('PENDING_CONFIRMATION',
 * 'DELIVERING', 'COMPLETED') — that appears to describe the Sub-Order's
 * fulfillment status instead of this parent-Order status; worth
 * reconciling naming with your team.
 */
public enum OrderStatus {
    CREATED,
    FRUIT_HELD,
    PENDING_PAYMENT,
    CONFIRMED,
    CANCELLED,
    EXPIRED
}
