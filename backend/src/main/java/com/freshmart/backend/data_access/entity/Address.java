package com.freshmart.backend.data_access.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A saved shipping address belonging to a customer (SDS "Address" table).
 * A customer may have many addresses, but at most one of them has
 * isDefault = true at any time — enforced in AddressServiceImpl, not here.
 */
@Entity
@Table(name = "address")
@Getter
@Setter
@NoArgsConstructor
public class Address extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "receiver_name", nullable = false, length = 255)
    private String receiverName;

    @Column(name = "receiver_phone", nullable = false, length = 255)
    private String receiverPhone;

    @Column(name = "province", nullable = false, length = 255)
    private String province;

    @Column(name = "district", nullable = false, length = 255)
    private String district;

    @Column(name = "ward", nullable = false, length = 255)
    private String ward;

    @Column(name = "specific_address", length = 255)
    private String specificAddress;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault = false;
}
