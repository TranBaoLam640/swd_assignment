package com.freshmart.backend.data_access.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Matches CartItem in the class diagram (cartItemId inherited from
 * {@link BaseEntity#getId()}; userId, productId, quantity). Per the
 * diagram there is no separate "Cart" entity — a customer's cart is simply
 * the set of CartItem rows where userId matches (see UC12 - View Shopping
 * Cart).
 *
 * <p>{@code productId} is a plain reference, not a JPA {@code @ManyToOne},
 * because the Product entity/module does not exist yet — convert to a real
 * relation once it does.
 *
 * <p>Note: the SDS class diagram's attribute table for CartItem
 * (quantityAdded/importPrice/receivedAt) appears to be a copy-paste error
 * from the Inventory/BatchReceipt class — those fields describe a stock
 * receipt, not a shopping cart line item. Only cartItemId/userId/productId/
 * quantity (matching the class's own description and methods) are modeled
 * here.
 */
@Entity
@Table(name = "cart_items",
        uniqueConstraints = @UniqueConstraint(name = "uk_cart_user_product", columnNames = {"user_id", "product_id"}))
@Getter
@Setter
@NoArgsConstructor
public class CartItem extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer quantity;
}
