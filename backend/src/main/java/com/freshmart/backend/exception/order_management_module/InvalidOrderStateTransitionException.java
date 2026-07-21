package com.freshmart.backend.exception.order_management_module;

import com.freshmart.backend.common.exception.BusinessException;
import com.freshmart.backend.enums.order_management_module.OrderStatus;

/** Thrown by {@code OrderStateMachine} when a requested status change is not allowed. */
public class InvalidOrderStateTransitionException extends BusinessException {

    private final Long orderId;
    private final OrderStatus currentStatus;
    private final OrderStatus targetStatus;

    public InvalidOrderStateTransitionException(Long orderId, OrderStatus currentStatus, OrderStatus targetStatus) {
        super("Cannot transition order " + orderId + " from " + currentStatus + " to " + targetStatus + ".");
        this.orderId = orderId;
        this.currentStatus = currentStatus;
        this.targetStatus = targetStatus;
    }

    public Long getOrderId() {
        return orderId;
    }

    public OrderStatus getCurrentStatus() {
        return currentStatus;
    }

    public OrderStatus getTargetStatus() {
        return targetStatus;
    }
}
