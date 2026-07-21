package com.freshmart.backend.mapper.order_management_module;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.freshmart.backend.data_access.entity.Order;
import com.freshmart.backend.data_access.entity.OrderItem;
import com.freshmart.backend.data_access.entity.Product;
import com.freshmart.backend.dto.response.order_management_module.OrderItemResponse;
import com.freshmart.backend.dto.response.order_management_module.OrderResponse;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order, List<OrderItem> items, Map<Long, Product> productsById) {
        List<OrderItemResponse> itemResponses = items.stream()
                .map(item -> toItemResponse(item, productsById.get(item.getProductId())))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getPaymentMethod(),
                order.getPaymentStatus(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCancelReason(),
                itemResponses,
                order.getCreatedAt()
        );
    }

    private OrderItemResponse toItemResponse(OrderItem item, Product product) {
        return new OrderItemResponse(
                item.getId(),
                item.getProductId(),
                product != null ? product.getProductName() : null,
                item.getQuantity(),
                item.getPriceAtPurchase(),
                item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity()))
        );
    }
}
