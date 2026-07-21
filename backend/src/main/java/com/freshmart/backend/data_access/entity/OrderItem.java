package com.freshmart.backend.data_access.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Matches order_item (SDS 2.2.12): one row per product line within an
 * Order. {@code quantity}/{@code priceAtPurchase} are snapshotted at
 * checkout time so a later Product price change never retroactively
 * alters a past order (BR-19: "purchased items, quantities, and unit
 * prices shall become immutable").
 *
 * <p>{@code orderId}/{@code productId} are plain Long references, not JPA
 * relations — same convention used by CartItem/Order elsewhere in this
 * codebase.
 */
@Entity
@Table(name = "order_item")
@Getter
@Setter
@NoArgsConstructor
public class OrderItem extends BaseEntity {

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "price_at_purchase", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceAtPurchase;
}
