package com.freshmart.backend.controller.address_management_module;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freshmart.backend.common.dto.ApiResponse;
import com.freshmart.backend.dto.request.address_management_module.AddressRequest;
import com.freshmart.backend.dto.response.address_management_module.AddressResponse;
import com.freshmart.backend.service.interfaces.address_management_module.AddressService;

import jakarta.validation.Valid;

/** Implements UC "Manage Shipping Address". Customer identity always comes from
 * the JWT principal, mirroring CartController. */
@RestController
@RequestMapping("/api/v1/customer/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    public ApiResponse<List<AddressResponse>> listAddresses(@AuthenticationPrincipal Long userId) {
        return ApiResponse.success(addressService.listAddresses(userId));
    }

    @PostMapping
    public ApiResponse<AddressResponse> createAddress(@AuthenticationPrincipal Long userId,
                                                        @Valid @RequestBody AddressRequest request) {
        return ApiResponse.success(201, "Address added", addressService.createAddress(userId, request));
    }

    @PutMapping("/{addressId}")
    public ApiResponse<AddressResponse> updateAddress(@AuthenticationPrincipal Long userId,
                                                        @PathVariable Long addressId,
                                                        @Valid @RequestBody AddressRequest request) {
        return ApiResponse.success(200, "Address updated",
                addressService.updateAddress(userId, addressId, request));
    }

    @PatchMapping("/{addressId}/default")
    public ApiResponse<AddressResponse> setDefault(@AuthenticationPrincipal Long userId,
                                                     @PathVariable Long addressId) {
        return ApiResponse.success(200, "Default address updated",
                addressService.setDefault(userId, addressId));
    }

    @DeleteMapping("/{addressId}")
    public ApiResponse<Void> deleteAddress(@AuthenticationPrincipal Long userId,
                                            @PathVariable Long addressId) {
        addressService.deleteAddress(userId, addressId);
        return ApiResponse.success(200, "Address deleted", null);
    }
}
