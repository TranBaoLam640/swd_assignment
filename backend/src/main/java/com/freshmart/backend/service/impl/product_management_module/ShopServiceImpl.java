package com.freshmart.backend.service.impl.product_management_module;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.freshmart.backend.common.exception.ResourceNotFoundException;
import com.freshmart.backend.data_access.entity.Shop;
import com.freshmart.backend.data_access.repository.authentication_and_user_account.UserRepository;
import com.freshmart.backend.data_access.repository.product_management_module.ShopRepository;
import com.freshmart.backend.dto.request.product_management_module.CreateShopRequest;
import com.freshmart.backend.dto.response.product_management_module.ShopResponse;
import com.freshmart.backend.mapper.product_management_module.ShopMapper;
import com.freshmart.backend.service.interfaces.product_management_module.ShopService;

@Service
public class ShopServiceImpl implements ShopService {

    private final ShopRepository shopRepository;
    private final UserRepository userRepository;
    private final ShopMapper shopMapper;

    public ShopServiceImpl(ShopRepository shopRepository, UserRepository userRepository, ShopMapper shopMapper) {
        this.shopRepository = shopRepository;
        this.userRepository = userRepository;
        this.shopMapper = shopMapper;
    }

    @Override
    public List<ShopResponse> listShops() {
        return shopRepository.findAll().stream()
                .map(shopMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ShopResponse createShop(CreateShopRequest request) {
        userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> ResourceNotFoundException.of("User", request.getOwnerId()));

        Shop shop = new Shop();
        shop.setOwnerId(request.getOwnerId());
        shop.setShopName(request.getShopName());
        shop.setShopAddress(request.getShopAddress());
        shop.setShopDescription(request.getShopDescription());
        shop.setStatus(request.getStatus());

        return shopMapper.toResponse(shopRepository.save(shop));
    }
}
