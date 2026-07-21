package com.freshmart.backend.exception.order_management_module;

import com.freshmart.backend.common.exception.BusinessException;

/** Thrown by UC33 - Checkout when the customer's cart has no items to check out. */
public class EmptyCartException extends BusinessException {
    public EmptyCartException() {
        super("Your shopping cart is empty.");
    }
}
