package com.freshmart.backend.dto.request.order_management_module;

import com.freshmart.backend.enums.order_management_module.PaymentMethod;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request body for UC33 - Checkout and Place Order.
 *
 * addressId is now required (Manage Shipping Address requirement: a
 * customer must pick — or add — one of their saved addresses before
 * placing an order). It must belong to the requesting customer; validated
 * in OrderServiceImpl via AddressService before the order is created. */
@Getter
@Setter
@NoArgsConstructor
public class CheckoutRequest {

    @NotNull
    private PaymentMethod paymentMethod;

    @NotNull(message = "Vui lòng chọn địa chỉ giao hàng.")
    private Long addressId;
}
