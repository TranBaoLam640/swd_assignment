package com.freshmart.backend.controller.product_management_module;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.freshmart.backend.common.dto.ApiResponse;
import com.freshmart.backend.dto.request.product_management_module.CreateShopRequest;
import com.freshmart.backend.dto.response.product_management_module.ShopResponse;
import com.freshmart.backend.service.interfaces.product_management_module.ShopService;

import jakarta.validation.Valid;

/** Manager shop lookup endpoints for product creation/catalog screens. */
@RestController
@RequestMapping("/api/v1/manager/shops")
public class ShopController {

    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @GetMapping
    public ApiResponse<List<ShopResponse>> listShops() {
        return ApiResponse.success(shopService.listShops());
    }

    @PostMapping
    public ApiResponse<ShopResponse> createShop(@Valid @RequestBody CreateShopRequest request) {
        return ApiResponse.success(201, "Shop created", shopService.createShop(request));
    }
}
