/**
 * Enums specific to the Payment Management module: {@code PaymentStatus}
 * (PENDING/SUCCESS/FAILED — used by both the {@code Payment} entity and
 * {@code Order.paymentStatus}). {@code PaymentMethod} (COD/ONLINE) lives
 * in enums.order_management_module instead, since it's chosen at
 * checkout time before any Payment row exists.
 */
package com.freshmart.backend.enums.payment_management_module;
