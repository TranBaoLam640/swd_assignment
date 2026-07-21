package com.freshmart.backend.data_access.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Matches the "category" table documented in SDS 2.2.7. {@code
 * Product.categoryId} is a plain Long pointing at this table's id — no JPA
 * {@code @ManyToOne} yet, same convention as the rest of the schema (see
 * Product's own Javadoc for why cross-entity references are kept as raw
 * columns instead of real relations).
 *
 * <p>Added to support UC09 - Browse &amp; Search Product's category filter
 * menu, which needs real category names (not just numeric ids) to show in
 * the dropdown — see CategoryController/CategorySeeder.
 */
@Entity
@Table(name = "category")
@Getter
@Setter
@NoArgsConstructor
public class Category extends BaseEntity {

    @Column(name = "category_name", nullable = false, length = 100)
    private String categoryName;
}
