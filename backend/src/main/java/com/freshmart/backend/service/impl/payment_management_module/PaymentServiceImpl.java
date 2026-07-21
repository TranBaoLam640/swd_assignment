package com.freshmart.backend.service.impl.payment_management_module;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.freshmart.backend.common.exception.ResourceNotFoundException;
import com.freshmart.backend.common.exception.UnauthorizedException;
import com.freshmart.backend.data_access.entity.Order;
import com.freshmart.backend.data_access.entity.Payment;
import com.freshmart.backend.data_access.repository.order_management_module.OrderRepository;
import com.freshmart.backend.data_access.repository.payment_management_module.PaymentRepository;
import com.freshmart.backend.enums.order_management_module.OrderStatus;
import com.freshmart.backend.enums.payment_management_module.PaymentStatus;
import com.freshmart.backend.exception.payment_management_module.OrderNotAwaitingPaymentException;
import com.freshmart.backend.external.payment.VNPayGateway;
import com.freshmart.backend.service.interfaces.order_management_module.OrderService;
import com.freshmart.backend.service.interfaces.payment_management_module.PaymentService;

/**
 * Implements UC33 Step 6 (redirect to Payment Gateway) + its alternative
 * flows (payment success / payment failed), scoped to VNPAY. See
 * {@link PaymentService}'s own Javadoc for the Return-vs-IPN trust model.
 *
 * <p>Never mutates {@code Order} directly — every status change goes
 * through {@code OrderService.confirmOnlinePayment}/{@code
 * failOnlinePayment}, which in turn goes through {@code
 * OrderStateMachine}. This module only reads {@code Order} (via {@code
 * OrderRepository}, the same cross-module repository reuse pattern
 * {@code OrderServiceImpl} itself already uses for Cart/Product) to
 * validate ownership, current status, and the amount actually owed.
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final VNPayGateway vnPayGateway;
    private final String frontendBaseUrl;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                               OrderRepository orderRepository,
                               OrderService orderService,
                               VNPayGateway vnPayGateway,
                               @Value("${app.frontend-base-url}") String frontendBaseUrl) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
        this.vnPayGateway = vnPayGateway;
        this.frontendBaseUrl = frontendBaseUrl;
    }

    @Override
    @Transactional
    public String createPaymentUrl(Long customerId, Long orderId, String clientIp) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> ResourceNotFoundException.of("Order", orderId));
        if (!order.getCustomerId().equals(customerId)) {
            throw new UnauthorizedException(403, "Access denied for order " + orderId + ".");
        }
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new OrderNotAwaitingPaymentException(orderId);
        }

        String txnRef = "ORDER" + orderId + "-" + System.currentTimeMillis();
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setGatewayTxnRef(txnRef);
        payment.setPaymentGateway("VNPAY");
        payment.setStatus(PaymentStatus.PENDING);
        paymentRepository.save(payment);

        return vnPayGateway.buildPaymentUrl(orderId, order.getTotalAmount(), txnRef, clientIp);
    }

    @Override
    @Transactional
    public String handleReturn(Map<String, String> params) {
        CallbackOutcome outcome = processCallback(params);
        if (outcome.orderId == null) {
            return frontendBaseUrl + "/";
        }
        return frontendBaseUrl + "/orders/" + outcome.orderId;
    }

    @Override
    @Transactional
    public Map<String, String> handleIpn(Map<String, String> params) {
        CallbackOutcome outcome = processCallback(params);
        Map<String, String> ack = new HashMap<>();
        ack.put("RspCode", outcome.rspCode);
        ack.put("Message", outcome.message);
        return ack;
    }

    /**
     * Shared by both handleReturn and handleIpn: verifies the signature,
     * looks up the Payment attempt by its gatewayTxnRef, checks
     * idempotency (the order must still be PENDING_PAYMENT), validates
     * the paid amount against the order's own totalAmount (never trust
     * an amount reported by the caller), then confirms or fails the
     * order through OrderService.
     *
     * <p>Note on the ack: VNPAY's RspCode communicates "did the merchant
     * server successfully process this notification", not "was the
     * payment itself successful" — a declined payment we've correctly
     * recorded as FAILED still acks "00"/"Confirm Success", otherwise
     * VNPAY will retry the callback indefinitely.
     */
    private CallbackOutcome processCallback(Map<String, String> params) {
        if (!vnPayGateway.verifySignature(params)) {
            return new CallbackOutcome(null, "97", "Invalid signature");
        }

        String txnRef = params.get("vnp_TxnRef");
        Payment payment = txnRef == null ? null : paymentRepository.findByGatewayTxnRef(txnRef).orElse(null);
        if (payment == null) {
            return new CallbackOutcome(null, "01", "Order not found");
        }

        Order order = orderRepository.findById(payment.getOrderId()).orElse(null);
        if (order == null) {
            return new CallbackOutcome(null, "01", "Order not found");
        }

        // Idempotency: VNPAY may call Return/IPN more than once for the same transaction.
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            return new CallbackOutcome(order.getId(), "02", "Order already confirmed");
        }

        BigDecimal receivedAmount;
        try {
            receivedAmount = new BigDecimal(params.getOrDefault("vnp_Amount", "0"))
                    .divide(BigDecimal.valueOf(100));
        } catch (NumberFormatException e) {
            return new CallbackOutcome(order.getId(), "04", "Invalid amount");
        }
        if (receivedAmount.compareTo(order.getTotalAmount()) != 0) {
            return new CallbackOutcome(order.getId(), "04", "Amount mismatch");
        }

        payment.setTransactionCode(params.get("vnp_TransactionNo"));
        payment.setAmountPaid(receivedAmount);
        payment.setPaymentMessage(params.get("vnp_OrderInfo"));
        payment.setRawResponse(params.toString());

        boolean success = "00".equals(params.get("vnp_ResponseCode"));
        if (success) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setPaidAt(Instant.now());
            paymentRepository.save(payment);
            orderService.confirmOnlinePayment(order.getId());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            orderService.failOnlinePayment(order.getId(),
                    "VNPAY payment failed (code " + params.get("vnp_ResponseCode") + ")");
        }

        return new CallbackOutcome(order.getId(), "00", "Confirm Success");
    }

    private static final class CallbackOutcome {
        private final Long orderId;
        private final String rspCode;
        private final String message;

        private CallbackOutcome(Long orderId, String rspCode, String message) {
            this.orderId = orderId;
            this.rspCode = rspCode;
            this.message = message;
        }
    }
}
