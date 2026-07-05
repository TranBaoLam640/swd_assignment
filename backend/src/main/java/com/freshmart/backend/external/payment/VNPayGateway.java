package com.freshmart.backend.external.payment;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Adapter around the VNPAY payment gateway ({@code <<External>>} in the SDS
 * Payment Management class diagram). Only
 * {@code PaymentServiceImpl} should call this class; it must never be used
 * directly by controllers or other modules.
 */
@Component
public class VNPayGateway {

    /**
     * Builds the VNPAY hosted-checkout redirect URL for the given order and
     * amount.
     */
    public String buildPaymentUrl(Long orderId, BigDecimal amount) {
        // TODO: build VNPAY query params, sign with hash secret, return full URL
        return null;
    }

    /**
     * Verifies the {@code vnp_SecureHash} on a VNPAY return/IPN payload.
     */
    public boolean verifySignature(String payload) {
        // TODO: recompute secure hash from payload and compare
        return false;
    }
}
