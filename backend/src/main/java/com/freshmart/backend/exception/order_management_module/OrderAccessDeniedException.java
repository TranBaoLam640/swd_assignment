package com.freshmart.backend.exception.order_management_module;

import com.freshmart.backend.common.exception.UnauthorizedException;

/**
 * Thrown by UC16/UC17 when a customer requests an order that exists but
 * belongs to a different customer. Deliberately reported as a distinct
 * "Access denied" (403) message rather than folded into a generic
 * not-found, per UC16's own alternative-sequence wording — unlike
 * {@code CartItemNotFoundException}, which merges the two on purpose.
 * Worth reconciling which convention your team actually wants.
 */
public class OrderAccessDeniedException extends UnauthorizedException {
    public OrderAccessDeniedException(Long orderId) {
        super(403, "Access denied for order " + orderId + ".");
    }
}
