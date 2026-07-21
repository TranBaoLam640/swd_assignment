package com.freshmart.backend.external.payment;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Adapter around the VNPAY payment gateway ({@code <<External>>} in the SDS
 * Payment Management class diagram). Only {@code PaymentServiceImpl}
 * should call this class; it must never be used directly by controllers
 * or other modules.
 *
 * <p>Config comes from {@code app.vnpay.*} (see application.yaml), same
 * pattern already used for {@code app.jwt.secret} — override via env vars
 * (VNPAY_TMN_CODE / VNPAY_HASH_SECRET / ...) or application-local.yaml.
 * Never commit real sandbox/production values. Register a free sandbox
 * merchant account at https://sandbox.vnpayment.vn to get a TmnCode +
 * HashSecret for testing.
 */
@Component
public class VNPayGateway {

    private static final DateTimeFormatter VNPAY_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Value("${app.vnpay.tmn-code}")
    private String tmnCode;

    @Value("${app.vnpay.hash-secret}")
    private String hashSecret;

    @Value("${app.vnpay.pay-url}")
    private String payUrl;

    @Value("${app.vnpay.return-url}")
    private String returnUrl;

    /**
     * Builds the VNPAY hosted-checkout redirect URL for one payment
     * attempt: the order's amount, our own unique {@code txnRef} (so the
     * Return/IPN callbacks can be matched back to a {@code Payment} row),
     * and the caller's IP (required by VNPAY).
     */
    public String buildPaymentUrl(Long orderId, BigDecimal amount, String txnRef, String clientIp) {
        Map<String, String> params = new TreeMap<>();
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", tmnCode);
        // VNPAY expects the amount multiplied by 100 (no decimal places).
        params.put("vnp_Amount", amount.multiply(BigDecimal.valueOf(100)).toBigInteger().toString());
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", txnRef);
        params.put("vnp_OrderInfo", "Thanh toan don hang " + orderId);
        params.put("vnp_OrderType", "other");
        params.put("vnp_Locale", "vn");
        params.put("vnp_ReturnUrl", returnUrl);
        params.put("vnp_IpAddr", (clientIp == null || clientIp.isBlank()) ? "127.0.0.1" : clientIp);
        params.put("vnp_CreateDate", LocalDateTime.now().format(VNPAY_DATE_FORMAT));

        return payUrl + "?" + buildSignedQuery(params);
    }

    /**
     * Verifies the {@code vnp_SecureHash} on a VNPAY return/IPN payload by
     * recomputing the hash from every other field and comparing.
     */
    public boolean verifySignature(Map<String, String> params) {
        String receivedHash = params.get("vnp_SecureHash");
        if (receivedHash == null || receivedHash.isBlank()) {
            return false;
        }
        Map<String, String> toHash = new TreeMap<>(params);
        toHash.remove("vnp_SecureHash");
        toHash.remove("vnp_SecureHashType");

        String computedHash = hmacSha512(hashSecret, buildHashData(toHash));
        return computedHash.equalsIgnoreCase(receivedHash);
    }

    private String buildSignedQuery(Map<String, String> params) {
        String secureHash = hmacSha512(hashSecret, buildHashData(params));

        StringBuilder query = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (query.length() > 0) {
                query.append('&');
            }
            query.append(urlEncode(entry.getKey())).append('=').append(urlEncode(entry.getValue()));
        }
        query.append("&vnp_SecureHash=").append(secureHash);
        return query.toString();
    }

    /** Builds the "key1=value1&key2=value2..." string VNPAY signs, over keys already sorted alphabetically (see the TreeMap callers use). */
    private String buildHashData(Map<String, String> params) {
        StringBuilder hashData = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getValue() == null || entry.getValue().isEmpty()) {
                continue;
            }
            if (hashData.length() > 0) {
                hashData.append('&');
            }
            hashData.append(urlEncode(entry.getKey())).append('=').append(urlEncode(entry.getValue()));
        }
        return hashData.toString();
    }

    private String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String hmacSha512(String key, String data) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA512");
            hmac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
            byte[] bytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("Failed to compute VNPAY secure hash", e);
        }
    }
}
