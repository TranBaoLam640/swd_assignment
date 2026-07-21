package com.freshmart.backend.dto.response.order_management_module;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.freshmart.backend.enums.order_management_module.OrderStatus;
import com.freshmart.backend.enums.order_management_module.PaymentMethod;
import com.freshmart.backend.enums.payment_management_module.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Full order detail, matching order (SDS 2.2.11) plus its line items —
 * used by both UC15 (history) and UC16 (detail).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private String cancelReason;
    private List<OrderItemResponse> items;
    private Instant createdAt;
}
