package com.freshmart.backend.exception.payment_management_module;

import com.freshmart.backend.common.exception.BusinessException;

/**
 * Thrown when a payment URL is requested for an order that isn't in
 * PENDING_PAYMENT (e.g. it's COD, already confirmed, or already
 * cancelled) — mirrors OrderStateMachine's own transition guard, at the
 * point where a customer action (not a status transition) is being
 * rejected.
 */
public class OrderNotAwaitingPaymentException extends BusinessException {
    public OrderNotAwaitingPaymentException(Long orderId) {
        super("Order " + orderId + " is not awaiting online payment.");
    }
}
