package com.freshmart.backend.enums.order_management_module;

/**
 * Guards the choice pseudostate in the Order state machine diagram
 * (branches to PENDING_PAYMENT vs straight to CONFIRMED).
 *
 * <p>{@code COD} corrects the diagram's {@code payment_method == "OCD"}
 * typo (confirmed against the order table's documented example value
 * 'COD' in SDS 2.2.11). {@code ONLINE} covers payment via the VNPAY
 * gateway; note the diagram literally guards on "ONLINE", which does not
 * match either documented order-table example value ('COD',
 * 'BANK_TRANSFER') — worth reconciling naming with your team.
 */
public enum PaymentMethod {
    COD,
    ONLINE
}
