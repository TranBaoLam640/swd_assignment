package com.freshmart.backend.service.interfaces.product_management_module;

import java.util.List;

import com.freshmart.backend.dto.request.product_management_module.CreateShopRequest;
import com.freshmart.backend.dto.response.product_management_module.ShopResponse;

public interface ShopService {
    List<ShopResponse> listShops();

    ShopResponse createShop(CreateShopRequest request);
}
