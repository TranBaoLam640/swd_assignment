package com.freshmart.backend.data_access.entity;

import com.freshmart.backend.enums.order_management_module.OrderStatus;
import com.freshmart.backend.exception.order_management_module.InvalidOrderStateTransitionException;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Central authority for parent-Order lifecycle transitions. Mirrors the
 * SDS 3.9 "Order state machine diagram" (top diagram, the parent Order),
 * scoped to CREATED through payment completion (CONFIRMED) — the
 * Sub-Order diagram (delivery, refund) is a separate state machine and is
 * out of scope here per current project scope (this project stops at the
 * VNPAY payment step).
 *
 * <p>All status changes must go through {@link #transition}; {@code
 * Order.status} has no public setter, so callers cannot bypass validation —
 * same pattern as the reference project's {@code BookingStateMachine}.
 *
 * <p>One transition was added on top of the diagram while wiring up
 * {@code OrderServiceImpl#cancelOrder}: {@code PENDING_PAYMENT ->
 * CANCELLED}. The diagram itself only allowed {@code PENDING_PAYMENT ->
 * PENDING_PAYMENT} (a self-loop that's never actually useful) plus
 * CONFIRMED/EXPIRED, which meant an online-payment order had no way to be
 * customer-cancelled while awaiting payment — that contradicts UC17
 * ("cancel an order when status is still pending") and BR-17 ("cancelled
 * payment transactions shall release reserved inventory"). Added the
 * missing edge rather than shipping Cancel Order broken for ONLINE
 * orders; nothing else in the diagram was changed.
 *
 * <p>Two naming issues were found in the diagram while implementing this
 * (both already flagged separately, restated here for traceability):
 * <ul>
 *   <li>The choice-pseudostate guard reads {@code payment_method == "OCD"}
 *       — a typo for "COD", corrected in {@link com.freshmart.backend.enums.order_management_module.PaymentMethod}.</li>
 *   <li>The other guard branch reads {@code payment_method == "ONLINE"},
 *       which doesn't literally match either order-table example value
 *       ('COD', 'BANK_TRANSFER') documented in SDS 2.2.11 — treated here as
 *       the general "pay via VNPAY gateway" bucket.</li>
 * </ul>
 */
@Component
public final class OrderStateMachine {

    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS = Map.of(
            OrderStatus.CREATED, Set.of(OrderStatus.FRUIT_HELD),
            OrderStatus.FRUIT_HELD, Set.of(
                    OrderStatus.PENDING_PAYMENT,
                    OrderStatus.CONFIRMED,
                    OrderStatus.CANCELLED,
                    OrderStatus.EXPIRED
            ),
            OrderStatus.PENDING_PAYMENT, Set.of(
                    OrderStatus.PENDING_PAYMENT,
                    OrderStatus.CANCELLED,
                    OrderStatus.CONFIRMED,
                    OrderStatus.EXPIRED
            ),
            OrderStatus.CONFIRMED, Set.of(OrderStatus.CANCELLED)
    );

    /**
     * CONFIRMED marks payment completion — the scope boundary this class
     * covers — but is intentionally not terminal: the diagram still allows
     * CONFIRMED -> CANCELLED (pre-fulfillment cancellation). Anything past
     * that (delivery, refund) belongs to the separate Sub-Order state
     * machine.
     */
    private static final Set<OrderStatus> TERMINAL_STATUSES = Set.of(
            OrderStatus.CANCELLED,
            OrderStatus.EXPIRED
    );

    public boolean canTransition(OrderStatus current, OrderStatus target) {
        if (current == null || target == null) {
            return false;
        }
        return ALLOWED_TRANSITIONS.getOrDefault(current, Set.of()).contains(target);
    }

    public void validateTransition(Long orderId, OrderStatus current, OrderStatus target) {
        if (!canTransition(current, target)) {
            throw new InvalidOrderStateTransitionException(orderId, current, target);
        }
    }

    public void initialize(Order order) {
        Objects.requireNonNull(order, "order must not be null");
        if (order.getStatus() == null) {
            order.applyStatus(OrderStatus.CREATED);
            return;
        }
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new InvalidOrderStateTransitionException(order.getId(), order.getStatus(), OrderStatus.CREATED);
        }
    }

    public void transition(Order order, OrderStatus target) {
        Objects.requireNonNull(order, "order must not be null");
        validateTransition(order.getId(), order.getStatus(), target);
        order.applyStatus(target);
    }

    public boolean isTerminal(OrderStatus status) {
        return status != null && TERMINAL_STATUSES.contains(status);
    }

    public Set<OrderStatus> allowedTargets(OrderStatus current) {
        return ALLOWED_TRANSITIONS.getOrDefault(current, Set.of());
    }
}
