package com.freshmart.backend.dto.response.address_management_module;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {
    private Long addressId;
    private String receiverName;
    private String receiverPhone;
    private String province;
    private String district;
    private String ward;
    private String specificAddress;
    private boolean defaultAddress;
}
