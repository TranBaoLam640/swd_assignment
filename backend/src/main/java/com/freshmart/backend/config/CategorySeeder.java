package com.freshmart.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.freshmart.backend.data_access.entity.Category;
import com.freshmart.backend.data_access.repository.product_management_module.CategoryRepository;

/**
 * Seeds a handful of demo fruit categories on startup if they don't already
 * exist, so the UC09 category filter menu has real options without needing
 * a separate category-admin UI yet (out of scope for now — managers can't
 * create/edit categories through the app, only via this seed list or the
 * DB directly).
 */
@Component
public class CategorySeeder implements CommandLineRunner {

    private static final String[] DEFAULT_CATEGORIES = {
            "Trái cây nhiệt đới",
            "Trái cây nhập khẩu",
            "Trái cây theo mùa",
            "Trái cây sấy & chế biến"
    };

    private final CategoryRepository categoryRepository;

    public CategorySeeder(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {
        for (String name : DEFAULT_CATEGORIES) {
            categoryRepository.findByCategoryName(name).orElseGet(() -> {
                Category category = new Category();
                category.setCategoryName(name);
                return categoryRepository.save(category);
            });
        }
    }
}
