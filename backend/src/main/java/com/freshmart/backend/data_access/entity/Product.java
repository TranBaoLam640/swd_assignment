package com.freshmart.backend.data_access.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Matches the "product" table exactly as documented in SDS 2.2.6:
 * productId is inherited from {@link BaseEntity#getId()}; shopId,
 * categoryId, productName, description, price, imageUrl, isActive are the
 * table's own columns.
 *
 * <p>{@code shopId}/{@code categoryId} are plain Long references, not JPA
 * {@code @ManyToOne}, because the Shop/Category entities don't exist yet —
 * convert to real relations once those modules are built.
 *
 * <p>Stock quantity is deliberately NOT here: SDS 2.2.8 "inventory" is a
 * separate one-to-one table, out of scope for this entity.
 *
 * <p>Note: SDS also documents a "product_category" junction table (2.2.7)
 * for a many-to-many Product&lt;-&gt;Category relationship, on top of this
 * table's own single {@code category_id} FK — the schema models both a
 * single primary category and a many-to-many tagging relation at once.
 * Only the single {@code categoryId} column is modeled here; the junction
 * table can be added as its own entity later if multi-category tagging is
 * actually needed — worth confirming with your team which one is the real
 * source of truth for a product's category.
 */
@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
public class Product extends BaseEntity {

    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    @Column(length = 255)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
