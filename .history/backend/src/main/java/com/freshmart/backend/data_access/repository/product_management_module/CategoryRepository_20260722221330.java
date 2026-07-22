package com.freshmart.backend.data_access.repository.product_management_module;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.freshmart.backend.data_access.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    /** Used by CategorySeeder to seed idempotently (skip if already present). */
    Optional<Category> findByCategoryName(String categoryName);
}
 