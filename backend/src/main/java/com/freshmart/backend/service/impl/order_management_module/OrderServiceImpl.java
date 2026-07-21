package com.freshmart.backend.service.impl.order_management_module;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.freshmart.backend.common.exception.ResourceNotFoundException;
import com.freshmart.backend.data_access.entity.CartItem;
import com.freshmart.backend.data_access.entity.Order;
import com.freshmart.backend.data_access.entity.OrderItem;
import com.freshmart.backend.data_access.entity.OrderStateMachine;
import com.freshmart.backend.data_access.entity.Product;
import com.freshmart.backend.data_access.repository.cart_management_module.CartItemRepository;
import com.freshmart.backend.data_access.repository.order_management_module.OrderItemRepository;
import com.freshmart.backend.data_access.repository.order_management_module.OrderRepository;
import com.freshmart.backend.data_access.repository.product_management_module.ProductRepository;
import com.freshmart.backend.dto.request.order_management_module.CancelOrderRequest;
import com.freshmart.backend.dto.request.order_management_module.CheckoutRequest;
import com.freshmart.backend.dto.response.order_management_module.OrderResponse;
import com.freshmart.backend.enums.order_management_module.OrderStatus;
import com.freshmart.backend.enums.order_management_module.PaymentMethod;
import com.freshmart.backend.enums.payment_management_module.PaymentStatus;
import com.freshmart.backend.exception.order_management_module.EmptyCartException;
import com.freshmart.backend.exception.order_management_module.OrderAccessDeniedException;
import com.freshmart.backend.exception.order_management_module.ProductUnavailableException;
import com.freshmart.backend.mapper.order_management_module.OrderMapper;
import com.freshmart.backend.service.interfaces.address_management_module.AddressService;
import com.freshmart.backend.service.interfaces.inventory_management_module.InventoryService;
import com.freshmart.backend.service.interfaces.order_management_module.OrderService;

/**
 * Implements UC33 (Checkout and Place Order) + UC15 (View Order History) +
 * UC16 (View Order Details) + UC17 (Cancel Order).
 *
 * <p>Deliberately simplified vs the full SRS/SDS design, consistent with
 * earlier scope decisions this session: one Order per checkout regardless
 * of how many shops its products belong to (no parent/child Sub-Order
 * split — that needs a Shop module and per-shop shipping, out of scope);
 * checkout runs synchronously up to CONFIRMED for COD, or PENDING_PAYMENT
 * for ONLINE — {@link #confirmOnlinePayment}, {@link #failOnlinePayment},
 * and {@link #expireStalePendingPayments} are the only ways an ONLINE
 * order leaves PENDING_PAYMENT: the first two are called exclusively by
 * {@code PaymentServiceImpl} once VNPAY's callback is verified, the third
 * by {@code OrderExpiryScheduler} on a timer, for the case where no
 * callback ever arrives at all (customer abandoned the VNPAY page) — never
 * by a controller directly.
 *
 * <p>Shipping address (Manage Shipping Address requirement): checkout now
 * requires {@code CheckoutRequest.addressId} and validates — via
 * {@link AddressService#getAddress} — that it exists and belongs to the
 * requesting customer before the order is created, so a customer can never
 * place an order using someone else's saved address. The Order/OrderItem
 * schema itself carries no shipping-address column (out of scope for this
 * change), so this is a pure guard, not a snapshot/copy of the address.
 *
 * <p>Stock handling: this treats "reserve" and "deduct" as the same step
 * (inventoryService.decreaseStock at checkout time) rather than a
 * separate soft-hold — the SDS inventory schema (one row per product) has
 * no reservation/hold column to track a soft hold separately, so an
 * actual deduction is the closest faithful implementation of BR-16
 * ("stock shall be temporarily locked during checkout"). Stock is only
 * ever given back (inventoryService.increaseStock, BR-17) by an explicit
 * customer cancel ({@link #cancelOrder}) or by the order genuinely timing
 * out ({@link #expireStalePendingPayments}) — NOT by a single failed
 * payment attempt; see {@link #failOnlinePayment} for why. The whole
 * checkout runs in one
 *
 * @Transactional method, so an InsufficientStockException partway through
 * rolls back everything (order, order items, and any stock already
 * decremented) — satisfying BR-18 ("an order cannot be finalized unless
 * stock deduction completed successfully").
 */
@Service
public class OrderServiceImpl implements OrderService {

    private static final String EXPIRY_REASON = "Hết hạn do quá thời gian chờ thanh toán";

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;
    private final OrderStateMachine orderStateMachine;
    private final OrderMapper orderMapper;
    private final AddressService addressService;

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            CartItemRepository cartItemRepository,
                            ProductRepository productRepository,
                            InventoryService inventoryService,
                            OrderStateMachine orderStateMachine,
                            OrderMapper orderMapper,
                            AddressService addressService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.inventoryService = inventoryService;
        this.orderStateMachine = orderStateMachine;
        this.orderMapper = orderMapper;
        this.addressService = addressService;
    }

    @Override
    @Transactional
    public OrderResponse checkout(Long customerId, CheckoutRequest request) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(customerId);
        if (cartItems.isEmpty()) {
            throw new EmptyCartException();
        }

        // Manage Shipping Address requirement: addressId must exist and belong
        // to this customer — throws AddressNotFoundException otherwise, which
        // also covers "customer somehow has none / sent a bogus id" since
        // AddressNotFoundException is thrown for both cases.
        addressService.getAddress(customerId, request.getAddressId());

        Map<Long, Product> productsById = productRepository
                .findAllById(cartItems.stream().map(CartItem::getProductId).toList())
                .stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // BR-14/BR-20: revalidate every line is still an active, existing product.
        for (CartItem item : cartItems) {
            Product product = productsById.get(item.getProductId());
            if (product == null || !Boolean.TRUE.equals(product.getIsActive())) {
                throw new ProductUnavailableException(item.getProductId());
            }
        }

        BigDecimal total = cartItems.stream()
                .map(item -> productsById.get(item.getProductId()).getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setCustomerId(customerId);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setTotalAmount(total);
        orderStateMachine.initialize(order);
        orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem item : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(item.getProductId());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPriceAtPurchase(productsById.get(item.getProductId()).getPrice());
            orderItems.add(orderItem);
        }
        orderItemRepository.saveAll(orderItems);

        // BR-14/BR-16/BR-18: revalidate + lock stock now, inside the same transaction —
        // if any line is short, InsufficientStockException rolls back the whole checkout.
        for (CartItem item : cartItems) {
            inventoryService.decreaseStock(item.getProductId(), item.getQuantity());
        }
        orderStateMachine.transition(order, OrderStatus.FRUIT_HELD);

        if (request.getPaymentMethod() == PaymentMethod.COD) {
            order.setPaymentStatus(PaymentStatus.PENDING);
            orderStateMachine.transition(order, OrderStatus.CONFIRMED);
        } else {
            order.setPaymentStatus(PaymentStatus.PENDING);
            orderStateMachine.transition(order, OrderStatus.PENDING_PAYMENT);
        }
        orderRepository.save(order);

        // Post-condition of UC33: clear the cart.
        cartItemRepository.deleteAll(cartItems);

        return orderMapper.toResponse(order, orderItems, productsById);
    }

    @Override
    public List<OrderResponse> listMyOrders(Long customerId) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId).stream()
                .map(this::toResponseWithItems)
                .toList();
    }

    @Override
    public OrderResponse getOrder(Long customerId, Long orderId) {
        return toResponseWithItems(findOwnedOrder(customerId, orderId));
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long customerId, Long orderId, CancelOrderRequest request) {
        Order order = findOwnedOrder(customerId, orderId);
        orderStateMachine.validateTransition(orderId, order.getStatus(), OrderStatus.CANCELLED);

        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        // BR-17: release whatever stock this order had reserved.
        for (OrderItem item : items) {
            inventoryService.increaseStock(item.getProductId(), item.getQuantity());
        }

        order.setCancelReason(request.getReason());
        orderStateMachine.transition(order, OrderStatus.CANCELLED);
        orderRepository.save(order);

        return orderMapper.toResponse(order, items, loadProducts(items));
    }

    @Override
    @Transactional
    public OrderResponse confirmOnlinePayment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> ResourceNotFoundException.of("Order", orderId));
        order.setPaymentStatus(PaymentStatus.SUCCESS);
        orderStateMachine.transition(order, OrderStatus.CONFIRMED);
        orderRepository.save(order);
        return toResponseWithItems(order);
    }

    /**
     * A single failed/declined VNPAY attempt does NOT cancel the order or
     * release its reserved stock — per the SDS state machine, "Pending
     * payment" has a self-loop on payment failure ("record failed payment,
     * allow retry"), distinct from an explicit customer cancel or a genuine
     * timeout. So this only records the attempt's outcome and stays in
     * PENDING_PAYMENT (via an explicit self-transition, validated the same
     * way as every other transition) so the customer can hit "Thanh toán
     * lại" and get a fresh VNPAY payment URL for the same still-reserved
     * order. Stock is only released by {@link #cancelOrder} or by
     * {@link #expireStalePendingPayments} once the hold genuinely times out.
     */
    @Override
    @Transactional
    public OrderResponse failOnlinePayment(Long orderId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> ResourceNotFoundException.of("Order", orderId));

        order.setPaymentStatus(PaymentStatus.FAILED);
        orderStateMachine.transition(order, OrderStatus.PENDING_PAYMENT);
        orderRepository.save(order);

        return toResponseWithItems(order);
    }

    @Override
    @Transactional
    public int expireStalePendingPayments(Instant cutoff) {
        List<Order> staleOrders = orderRepository.findByStatusAndCreatedAtBefore(OrderStatus.PENDING_PAYMENT, cutoff);

        int expiredCount = 0;
        for (Order order : staleOrders) {
            // Re-check the transition is still legal (defensive: a stale
            // order could theoretically have been confirmed/cancelled by a
            // callback in between the query and this loop iteration under
            // heavy concurrency); skip rather than throw so one bad row
            // doesn't abort the whole sweep.
            if (!orderStateMachine.canTransition(order.getStatus(), OrderStatus.EXPIRED)) {
                continue;
            }

            List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
            // BR-17: release whatever stock this order had reserved.
            for (OrderItem item : items) {
                inventoryService.increaseStock(item.getProductId(), item.getQuantity());
            }

            order.setPaymentStatus(PaymentStatus.FAILED);
            order.setCancelReason(EXPIRY_REASON);
            orderStateMachine.transition(order, OrderStatus.EXPIRED);
            orderRepository.save(order);
            expiredCount++;
        }

        return expiredCount;
    }

    private Order findOwnedOrder(Long customerId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> ResourceNotFoundException.of("Order", orderId));
        if (!order.getCustomerId().equals(customerId)) {
            throw new OrderAccessDeniedException(orderId);
        }
        return order;
    }

    private OrderResponse toResponseWithItems(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        return orderMapper.toResponse(order, items, loadProducts(items));
    }

    private Map<Long, Product> loadProducts(List<OrderItem> items) {
        return productRepository
                .findAllById(items.stream().map(OrderItem::getProductId).toList())
                .stream()
                .collect(Collectors.toMap(Product::getId, p -> p));
    }
}
