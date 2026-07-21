package com.freshmart.backend.service.interfaces.order_management_module;

import java.time.Instant;
import java.util.List;

import com.freshmart.backend.dto.request.order_management_module.CancelOrderRequest;
import com.freshmart.backend.dto.request.order_management_module.CheckoutRequest;
import com.freshmart.backend.dto.response.order_management_module.OrderResponse;

/**
 * Implements UC33 (Checkout and Place Order) + UC15 (View Order History) +
 * UC16 (View Order Details) + UC17 (Cancel Order), simplified per this
 * project's scope (see OrderServiceImpl's Javadoc).
 */
public interface OrderService {

    /** UC33: converts the customer's current cart into a new Order, reserving stock and clearing the cart. */
    OrderResponse checkout(Long customerId, CheckoutRequest request);

    /** UC15: orders belonging to the customer, newest first. */
    List<OrderResponse> listMyOrders(Long customerId);

    /** UC16: full detail of one order; throws if it doesn't belong to the customer. */
    OrderResponse getOrder(Long customerId, Long orderId);

    /** UC17: cancels an order still in a cancellable status, releasing any reserved stock. */
    OrderResponse cancelOrder(Long customerId, Long orderId, CancelOrderRequest request);

    /**
     * Called only by the Payment module once VNPAY confirms a payment
     * succeeded — moves PENDING_PAYMENT -> CONFIRMED. No customer/ownership
     * check here; the Payment module already validated the callback.
     */
    OrderResponse confirmOnlinePayment(Long orderId);

    /**
     * Called only by the Payment module when VNPAY reports a payment
     * failed — moves PENDING_PAYMENT -> CANCELLED and releases any
     * reserved stock (BR-17), same as a customer-initiated cancel.
     */
    OrderResponse failOnlinePayment(Long orderId, String reason);

    /**
     * Called only by {@code OrderExpiryScheduler}: expires every order
     * still PENDING_PAYMENT with a {@code createdAt} before {@code cutoff}
     * (customer abandoned VNPAY before any Return/IPN ever arrived), moving
     * each PENDING_PAYMENT -> EXPIRED and releasing its reserved stock —
     * same release logic as failOnlinePayment/cancelOrder, just triggered
     * by a timeout instead of an explicit signal. Returns how many orders
     * were expired, purely for logging.
     */
    int expireStalePendingPayments(Instant cutoff);
}
