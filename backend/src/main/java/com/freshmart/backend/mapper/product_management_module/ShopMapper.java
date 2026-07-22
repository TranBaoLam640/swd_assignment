package com.freshmart.backend.mapper.product_management_module;

import org.springframework.stereotype.Component;

import com.freshmart.backend.data_access.entity.Shop;
import com.freshmart.backend.dto.response.product_management_module.ShopResponse;

@Component
public class ShopMapper {

    public ShopResponse toResponse(Shop shop) {
        return new ShopResponse(
                shop.getId(),
                shop.getOwnerId(),
                shop.getShopName(),
                shop.getShopAddress(),
                shop.getShopDescription(),
                shop.getStatus()
        );
    }
}
