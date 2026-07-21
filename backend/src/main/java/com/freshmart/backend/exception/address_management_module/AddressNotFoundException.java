package com.freshmart.backend.exception.address_management_module;

import com.freshmart.backend.common.exception.BusinessException;

/** Thrown when an addressId doesn't exist, or exists but belongs to a different
 * customer (both cases return the same message so a malicious/curious caller can't
 * use this endpoint to probe which address ids exist for other users). */
public class AddressNotFoundException extends BusinessException {
    public AddressNotFoundException(Long addressId) {
        super("Không tìm thấy địa chỉ với id " + addressId + " hoặc địa chỉ này không thuộc về bạn.");
    }
}
