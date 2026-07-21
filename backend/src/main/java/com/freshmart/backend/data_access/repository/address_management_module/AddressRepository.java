package com.freshmart.backend.data_access.repository.address_management_module;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.freshmart.backend.data_access.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByUserId(Long userId);

    Optional<Address> findByUserIdAndIsDefaultTrue(Long userId);

    boolean existsByUserId(Long userId);

    /** Bulk-unset the current default (if any) before a new address takes its place. */
    @Modifying
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.userId = :userId AND a.isDefault = true")
    void clearDefaultForUser(@Param("userId") Long userId);
}
