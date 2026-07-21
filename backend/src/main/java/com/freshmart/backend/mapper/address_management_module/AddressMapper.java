package com.freshmart.backend.mapper.address_management_module;

import org.springframework.stereotype.Component;

import com.freshmart.backend.data_access.entity.Address;
import com.freshmart.backend.dto.response.address_management_module.AddressResponse;

@Component
public class AddressMapper {

    public AddressResponse toResponse(Address address) {
        return new AddressResponse(
                address.getId(),
                address.getReceiverName(),
                address.getReceiverPhone(),
                address.getProvince(),
                address.getDistrict(),
                address.getWard(),
                address.getSpecificAddress(),
                address.isDefault()
        );
    }
}
