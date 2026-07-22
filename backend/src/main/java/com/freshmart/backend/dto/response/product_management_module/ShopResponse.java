package com.freshmart.backend.dto.response.product_management_module;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Response for manager shop selection menus. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShopResponse {
    private Long shopId;
    private Long ownerId;
    private String shopName;
    private String shopAddress;
    private String shopDescription;
    private String status;
}
