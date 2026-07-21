package com.freshmart.backend.service.interfaces.payment_management_module;

import java.util.Map;

/**
 * Implements UC33 Step 6 (redirect to Payment Gateway) and its alternative
 * flows (payment success / payment failed), scoped to VNPAY.
 */
public interface PaymentService {

    /**
     * Generates a new VNPAY payment URL for the given order, creating a
     * new Payment attempt row. Only the order's own customer may call
     * this, and only while the order is PENDING_PAYMENT.
     */
    String createPaymentUrl(Long customerId, Long orderId, String clientIp);

    /**
     * Handles the browser Return URL redirect (customer's browser lands
     * here after paying on VNPAY). Verifies the signature and — if
     * valid — applies the same idempotent status update as the IPN
     * callback, since a signed Return payload is just as trustworthy as
     * an IPN one; IPN stays the authoritative channel because it doesn't
     * depend on the customer's browser completing the redirect. Returns a
     * frontend URL to redirect the browser to.
     */
    String handleReturn(Map<String, String> params);

    /**
     * Handles the authoritative server-to-server IPN callback. Idempotent
     * (safe to call more than once for the same transaction). Returns the
     * exact {@code {"RspCode": "...", "Message": "..."}} body VNPAY
     * requires — never wrapped in {@code ApiResponse}.
     */
    Map<String, String> handleIpn(Map<String, String> params);
}
