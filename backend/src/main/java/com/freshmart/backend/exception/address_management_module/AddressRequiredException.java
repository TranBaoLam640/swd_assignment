package com.freshmart.backend.exception.address_management_module;

import com.freshmart.backend.common.exception.BusinessException;

/** Thrown when a customer with zero saved addresses tries to do something that
 * requires a shipping address (e.g. place an order) before adding one. Backs the
 * requirement that a customer's very first address is mandatory. */
public class AddressRequiredException extends BusinessException {
    public AddressRequiredException() {
        super("Bạn chưa có địa chỉ giao hàng nào. Vui lòng thêm địa chỉ trước khi tiếp tục.");
    }
}
