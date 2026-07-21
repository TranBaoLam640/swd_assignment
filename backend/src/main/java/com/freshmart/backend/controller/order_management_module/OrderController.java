package com.freshmart.backend.controller.order_management_module;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freshmart.backend.common.dto.ApiResponse;
import com.freshmart.backend.dto.request.order_management_module.CancelOrderRequest;
import com.freshmart.backend.dto.request.order_management_module.CheckoutRequest;
import com.freshmart.backend.dto.response.order_management_module.OrderResponse;
import com.freshmart.backend.service.interfaces.order_management_module.OrderService;

import jakarta.validation.Valid;

/**
 * Implements UC33, UC15-UC17. Customer identity always comes from the JWT
 * principal (same pattern as CartController). Already covered by
 * SecurityConfig's existing "/api/v1/customer/**" -> CUSTOMER rule, no
 * SecurityConfig change needed.
 */
@RestController
@RequestMapping("/api/v1/customer/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    public ApiResponse<OrderResponse> checkout(@AuthenticationPrincipal Long userId,
                                                @Valid @RequestBody CheckoutRequest request) {
        return ApiResponse.success(201, "Order placed", orderService.checkout(userId, request));
    }

    @GetMapping
    public ApiResponse<List<OrderResponse>> listMyOrders(@AuthenticationPrincipal Long userId) {
        return ApiResponse.success(orderService.listMyOrders(userId));
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponse> getOrder(@AuthenticationPrincipal Long userId,
                                                @PathVariable Long orderId) {
        return ApiResponse.success(orderService.getOrder(userId, orderId));
    }

    @PutMapping("/{orderId}/cancel")
    public ApiResponse<OrderResponse> cancelOrder(@AuthenticationPrincipal Long userId,
                                                   @PathVariable Long orderId,
                                                   @Valid @RequestBody CancelOrderRequest request) {
        return ApiResponse.success(200, "Order cancelled", orderService.cancelOrder(userId, orderId, request));
    }
}
