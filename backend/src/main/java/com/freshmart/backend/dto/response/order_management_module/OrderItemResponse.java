package com.freshmart.backend.dto.response.order_management_module;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.freshmart.backend.enums.product_management_module.PriceUnit;

/** One product line within an order, matching order_item (SDS 2.2.12). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    private Long orderItemId;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal priceAtPurchase;
    private PriceUnit priceUnit;
    private Integer priceQuantityGrams;
    private BigDecimal subtotal;
}
