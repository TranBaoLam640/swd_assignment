package com.freshmart.backend.data_access.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Matches the "inventory" table exactly as documented in SDS 2.2.8:
 * inventoryId is inherited from {@link BaseEntity#getId()}; productId and
 * stockQuantity are the table's own columns. The table's documented
 * "last_updated" column is covered by {@link BaseEntity#getUpdatedAt()}
 * (JPA auditing already stamps it on every save), so it isn't duplicated
 * as a separate field here.
 *
 * <p>Strict one-to-one with Product — "Each product has exactly one
 * corresponding record" per SDS 2.2.8 — enforced by the unique constraint
 * on {@code product_id}. {@code productId} is a plain Long, not
 * {@code @OneToOne}, same reasoning as Product's own shopId/categoryId:
 * kept simple until there's a reason to navigate the relation in code.
 */
@Entity
@Table(name = "inventory",
        uniqueConstraints = @UniqueConstraint(name = "uk_inventory_product", columnNames = "product_id"))
@Getter
@Setter
@NoArgsConstructor
public class Inventory extends BaseEntity {

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity = 0;
}
