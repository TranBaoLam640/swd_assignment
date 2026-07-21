package com.freshmart.backend.exception.inventory_management_module;

import com.freshmart.backend.common.exception.BusinessException;

/**
 * Thrown when a stock decrease would push stockQuantity below 0. Matches
 * BR-15 ("The system shall prevent negative inventory quantities under
 * all conditions") and UC29's "Stock quantity cannot be negative" message.
 */
public class InsufficientStockException extends BusinessException {
    public InsufficientStockException(Long productId, int currentStock, int requestedDecrease) {
        super("Cannot decrease stock for product " + productId + ": current stock is "
                + currentStock + ", requested decrease is " + requestedDecrease + ".");
    }
}
