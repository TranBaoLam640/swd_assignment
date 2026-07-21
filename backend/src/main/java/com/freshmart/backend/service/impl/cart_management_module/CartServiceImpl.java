package com.freshmart.backend.service.impl.cart_management_module;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.freshmart.backend.data_access.entity.CartItem;
import com.freshmart.backend.data_access.repository.cart_management_module.CartItemRepository;
import com.freshmart.backend.dto.request.cart_management_module.AddToCartRequest;
import com.freshmart.backend.dto.request.cart_management_module.UpdateCartItemRequest;
import com.freshmart.backend.dto.response.cart_management_module.CartItemResponse;
import com.freshmart.backend.exception.cart_management_module.CartItemNotFoundException;
import com.freshmart.backend.exception.cart_management_module.CartQuantityExceedsStockException;
import com.freshmart.backend.mapper.cart_management_module.CartItemMapper;
import com.freshmart.backend.service.interfaces.cart_management_module.CartService;
import com.freshmart.backend.service.interfaces.inventory_management_module.InventoryService;

/**
 * Implements UC11-UC14. addToCart()/updateQuantity() both re-check the
 * requested quantity against Inventory.stockQuantity server-side —
 * mirrors the same check already done client-side (ProductListPage /
 * ProductDetailPage) so a request that bypasses the UI, or a stale client
 * that hasn't refreshed stock, can't push a cart line above what the shop
 * actually has.
 */
@Service
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;
    private final InventoryService inventoryService;

    public CartServiceImpl(CartItemRepository cartItemRepository,
                            CartItemMapper cartItemMapper,
                            InventoryService inventoryService) {
        this.cartItemRepository = cartItemRepository;
        this.cartItemMapper = cartItemMapper;
        this.inventoryService = inventoryService;
    }

    @Override
    @Transactional
    public CartItemResponse addToCart(Long userId, AddToCartRequest request) {
        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, request.getProductId())
                .orElseGet(() -> {
                    CartItem created = new CartItem();
                    created.setUserId(userId);
                    created.setProductId(request.getProductId());
                    created.setQuantity(0);
                    return created;
                });

        int newQuantity = cartItem.getQuantity() + request.getQuantity();
        checkStock(request.getProductId(), newQuantity);

        cartItem.setQuantity(newQuantity);
        cartItemRepository.save(cartItem);
        return cartItemMapper.toResponse(cartItem);
    }

    @Override
    public List<CartItemResponse> viewCart(Long userId) {
        return cartItemRepository.findByUserId(userId).stream()
                .map(cartItemMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CartItemResponse updateQuantity(Long userId, Long cartItemId, UpdateCartItemRequest request) {
        CartItem cartItem = findOwnedCartItem(userId, cartItemId);

        if (request.getQuantity() <= 0) {
            cartItemRepository.delete(cartItem);
            return null;
        }

        checkStock(cartItem.getProductId(), request.getQuantity());

        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);
        return cartItemMapper.toResponse(cartItem);
    }

    @Override
    @Transactional
    public boolean removeItem(Long userId, Long cartItemId) {
        CartItem cartItem = findOwnedCartItem(userId, cartItemId);
        cartItemRepository.delete(cartItem);
        return true;
    }

    private void checkStock(Long productId, int requestedQuantity) {
        int stock = inventoryService.getByProduct(productId).getStockQuantity();
        if (requestedQuantity > stock) {
            throw new CartQuantityExceedsStockException(stock);
        }
    }

    private CartItem findOwnedCartItem(Long userId, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CartItemNotFoundException(cartItemId));
        if (!cartItem.getUserId().equals(userId)) {
            throw new CartItemNotFoundException(cartItemId);
        }
        return cartItem;
    }
}
