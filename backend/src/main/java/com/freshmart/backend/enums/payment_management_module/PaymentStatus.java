package com.freshmart.backend.enums.payment_management_module;

/**
 * Matches the literal payment_status values set by the Order state
 * machine diagram's transition actions (PENDING / SUCCESS / FAILED).
 *
 * <p>Note: this differs from the payment_status example values documented
 * on the order table in SDS 2.2.11 ('PENDING', 'PAID', 'REFUNDED') —
 * another naming inconsistency between the diagram and the schema doc,
 * worth reconciling with your team.
 */
public enum PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED
}
