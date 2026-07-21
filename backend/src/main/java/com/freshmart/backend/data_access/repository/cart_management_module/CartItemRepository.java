package com.freshmart.backend.data_access.repository.cart_management_module;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.freshmart.backend.data_access.entity.CartItem;

/** Matches CartItem repository needs for UC11-UC14. */
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUserId(Long userId);

    Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);
}
