package com.freshmart.backend.data_access.entity;

import java.math.BigDecimal;

import com.freshmart.backend.enums.order_management_module.OrderStatus;
import com.freshmart.backend.enums.order_management_module.PaymentMethod;
import com.freshmart.backend.enums.payment_management_module.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Parent order: the checkout/payment container created when a customer
 * places an order (per SDS 2.2.11, may be split into per-shop Sub-Orders —
 * out of scope here, this project models a single order per checkout
 * regardless of how many shops its products come from). Lifecycle is
 * governed exclusively by {@link OrderStateMachine}; see SDS 3.9 "Order
 * state machine diagram".
 *
 * <p>Table named "orders" (not "order") to avoid a MySQL reserved-word
 * conflict — same reasoning as {@link User} vs the "users" table.
 *
 * <p>{@code customerId} and {@code totalAmount} were added now that the
 * Order module is actually wired up to Checkout (UC33) — this class
 * started out "minimal" (state-machine fields only) per its original
 * Javadoc. {@code cancelReason} backs UC17's required cancellation-reason
 * field. Still no address_id/shipping_method/tracking_code — Address and
 * OrderTracking stay descoped per your earlier decision.
 */
@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order extends BaseEntity {

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 30)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", length = 30)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Setter(AccessLevel.NONE)
    private OrderStatus status = OrderStatus.CREATED;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "cancel_reason", length = 255)
    private String cancelReason;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    /** Only {@link OrderStateMachine} may call this — see its class Javadoc. */
    void applyStatus(OrderStatus status) {
        this.status = status;
    }
}
