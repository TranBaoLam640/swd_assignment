package com.freshmart.backend.service.impl.order_management_module;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.freshmart.backend.service.interfaces.order_management_module.OrderService;

import lombok.extern.slf4j.Slf4j;

/**
 * Background sweep for a UC33 alternative flow that the Return/IPN
 * callbacks alone can't cover: the customer opens the VNPAY payment page
 * and simply never completes it (closes the tab, loses connection, etc.).
 * In that case neither Return nor IPN is ever called, so the order would
 * otherwise sit in PENDING_PAYMENT forever with its stock permanently
 * locked (BR-16/BR-17).
 *
 * <p>Runs on a fixed interval ({@code app.order.expiry-check-interval-ms})
 * and expires any order still PENDING_PAYMENT older than {@code
 * app.order.pending-payment-timeout-minutes}, via {@link
 * OrderService#expireStalePendingPayments}, which releases the stock and
 * transitions PENDING_PAYMENT -> EXPIRED — an edge OrderStateMachine
 * already modeled but that nothing previously ever triggered.
 *
 * <p>timeoutMinutes is a double (not long) so demo/test configs can use
 * fractional values like 0.5 (30 seconds) — computed via minusMillis
 * rather than ChronoUnit.MINUTES, which only accepts whole numbers.
 *
 * <p>Requires {@code @EnableScheduling} on {@code BackendApplication} —
 * added alongside this class.
 */
@Slf4j
@Component
public class OrderExpiryScheduler {

    private final OrderService orderService;
    private final double timeoutMinutes;

    public OrderExpiryScheduler(OrderService orderService,
                                 @Value("${app.order.pending-payment-timeout-minutes}") double timeoutMinutes) {
        this.orderService = orderService;
        this.timeoutMinutes = timeoutMinutes;
    }

    @Scheduled(fixedRateString = "${app.order.expiry-check-interval-ms}")
    public void expireStalePendingPayments() {
        long timeoutMillis = Math.round(timeoutMinutes * 60_000);
        Instant cutoff = Instant.now().minusMillis(timeoutMillis);
        int expired = orderService.expireStalePendingPayments(cutoff);
        if (expired > 0) {
            log.info("Expired {} order(s) stuck in PENDING_PAYMENT for over {} minute(s)", expired, timeoutMinutes);
        }
    }
}
