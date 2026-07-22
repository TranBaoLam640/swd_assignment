package com.freshmart.backend.data_access.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Matches the "shop" table: shopId is inherited from {@link BaseEntity#getId()};
 * ownerId references the owning user, while the remaining fields describe the
 * shop profile and operational status.
 */
@Entity
@Table(name = "shop")
@Getter
@Setter
@NoArgsConstructor
public class Shop extends BaseEntity {

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "shop_name", nullable = false, unique = true, length = 255)
    private String shopName;

    @Column(name = "shop_address", nullable = false, unique = true, length = 255)
    private String shopAddress;

    @Column(name = "shop_description", length = 255)
    private String shopDescription;

    @Column(nullable = false, length = 255)
    private String status;
}
