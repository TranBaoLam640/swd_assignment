package com.freshmart.backend.service.interfaces.address_management_module;

import java.util.List;

import com.freshmart.backend.dto.request.address_management_module.AddressRequest;
import com.freshmart.backend.dto.response.address_management_module.AddressResponse;

/** Implements the "Manage Shipping Address" use case: a customer keeps a book of
 * saved addresses. The very first address they ever save is automatically the
 * default (there's nothing to choose between yet); from the second address on,
 * the customer explicitly picks which one is default and every other address is
 * unset when a new default is chosen. */
public interface AddressService {

    List<AddressResponse> listAddresses(Long userId);

    AddressResponse createAddress(Long userId, AddressRequest request);

    AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request);

    AddressResponse setDefault(Long userId, Long addressId);

    void deleteAddress(Long userId, Long addressId);

    /** True when the customer has zero saved addresses — the frontend uses this to
     * force the "add address" form before checkout instead of showing a picker. */
    boolean hasNoAddress(Long userId);

    /** Returns the address only if it exists AND belongs to this customer —
     * otherwise throws AddressNotFoundException. Used by OrderServiceImpl.checkout
     * to make sure a customer can't place an order with someone else's addressId. */
    AddressResponse getAddress(Long userId, Long addressId);
}
